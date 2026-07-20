# Deployment — Neon + Railway + Vercel

Production layout for Notes API:

```text
Browser → Vercel (frontend/) → Railway (Spring Boot API) → Neon (PostgreSQL)
```

Local Docker Compose (app + Postgres, same-origin UI) remains available for development. See the root [README](../README.md).

---

## 1. Neon (database)

1. Create a project at [neon.tech](https://neon.tech).
2. Create a database (or use the default).
3. Open **Connection details** and copy the connection string.
4. Convert it to a JDBC URL for Spring:

| Neon URI | JDBC |
| -------- | ---- |
| `postgresql://USER:PASSWORD@HOST/DB?sslmode=require` | `jdbc:postgresql://HOST/DB?sslmode=require` |

Put the user and password in separate variables (do not embed credentials only in the URL if you prefer Spring’s split properties):

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://ep-xxxx.region.aws.neon.tech/neondb?sslmode=require
SPRING_DATASOURCE_USERNAME=neondb_owner
SPRING_DATASOURCE_PASSWORD=<neon-password>
```

Prefer Neon’s **pooled** host for serverless-friendly connection limits when available; either works with this app. Schema is created/updated by Hibernate (`ddl-auto=update`) on first start.

---

## 2. Railway (backend)

1. Create a new project from this GitHub repository.
2. Set the service root / Dockerfile:
   - **Root Directory:** `note-app`  
   - **Dockerfile path:** `Dockerfile` (inside `note-app`)
3. Add variables:

| Variable | Example / notes |
| -------- | --------------- |
| `SPRING_DATASOURCE_URL` | Neon JDBC URL with `sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Neon user |
| `SPRING_DATASOURCE_PASSWORD` | Neon password |
| `JWT_SECRET` | Long random secret (not the repo default) |
| `JWT_EXPIRATION` | `86400000` (optional) |
| `CORS_ALLOWED_ORIGINS` | Your Vercel origin(s), comma-separated — set after step 3 if needed |
| `PORT` | Set automatically by Railway; app reads `server.port=${PORT:8080}` |

4. Deploy and copy the public HTTPS URL, e.g. `https://notes-api-production.up.railway.app` (no trailing slash).
5. Confirm health: open `/swagger-ui.html` or `POST /api/auth/register`.

### If the service crashes after ~1 minute

1. Open Railway → service → latest deployment → **View logs**.
2. Look for:
   - `OutOfMemoryError` / `Killed` → memory (Dockerfile already sets `-Xmx256m`; raise Railway memory or keep heap low)
   - `Connection refused` / `FATAL: password authentication` / `ssl` → Neon URL/user/password wrong, or missing `sslmode=require`
   - `Failed to configure a DataSource` → `SPRING_DATASOURCE_*` variables not set on the Railway service
3. Confirm **Variables** are on the **same service** that runs the Dockerfile (not only at project level without sharing).
4. Redeploy after fixing.

---

## 3. Vercel (frontend)

1. Import the same GitHub repository in [Vercel](https://vercel.com).
2. Configure the project:
   - **Root Directory:** `frontend`
   - **Framework Preset:** Other
   - **Build Command:** `node write-config.js` (from `vercel.json`)
   - **Output Directory:** `.`
3. Set environment variable:

| Variable | Value |
| -------- | ----- |
| `NOTES_API_BASE` | Railway public URL, e.g. `https://notes-api-production.up.railway.app` |

4. Deploy. The build writes `config.js` with `window.NOTES_API_BASE`.
5. Copy the Vercel URL, e.g. `https://notes-xxx.vercel.app`.

---

## 4. Connect CORS

On Railway, set (or update):

```text
CORS_ALLOWED_ORIGINS=https://notes-xxx.vercel.app
```

Add more origins with commas if needed (preview deployments, custom domain). Redeploy the Railway service so the new value is picked up.

Then open the Vercel URL, register, log in, and create a note.

---

## Environment checklist

### Railway

```text
PORT=<railway-managed>
SPRING_DATASOURCE_URL=jdbc:postgresql://...neon.tech/neondb?sslmode=require
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
JWT_SECRET=<strong-random-secret>
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app
```

### Vercel

```text
NOTES_API_BASE=https://your-api.up.railway.app
```

### Local split frontend (optional)

In [`frontend/config.js`](../frontend/config.js):

```js
window.NOTES_API_BASE = "http://localhost:8080";
```

Serve `frontend/` with any static server. Ensure the API allows `http://localhost:*` (default when `CORS_ALLOWED_ORIGINS` is empty) or list your exact origin.

---

## Troubleshooting

| Symptom | Check |
| ------- | ----- |
| Browser CORS error | `CORS_ALLOWED_ORIGINS` matches the Vercel origin exactly (scheme + host, no path) |
| `Failed to fetch` | `NOTES_API_BASE` has no trailing slash; Railway service is public HTTPS |
| DB connection refused / SSL | JDBC URL includes `sslmode=require`; user/password match Neon |
| 401 on every note call | Login again; JWT secret changed on Railway invalidates old tokens |
| Wrong port on Railway | App must use `server.port=${PORT:8080}` (already configured) |

---

## Security notes

- Never commit real Neon passwords, JWT secrets, or production `.env` files.
- Override the default `JWT_SECRET` before any shared deployment — see [SECURITY.md](../SECURITY.md).
- Restrict `CORS_ALLOWED_ORIGINS` to your Vercel (and custom) domains only.
