# Notes frontend (Vercel)

Static HTML/CSS/JS client for the Notes API.

## Local

1. Start the API (H2 or PostgreSQL) on port 8080.
2. Point this UI at the API by editing `config.js`:

```js
window.NOTES_API_BASE = "http://localhost:8080";
```

3. Serve this folder, e.g. `npx --yes serve .` or open `index.html` via a simple static server.

## Vercel

1. Import the GitHub repo in Vercel.
2. Set **Root Directory** to `frontend`.
3. Framework Preset: **Other**.
4. Set environment variable `NOTES_API_BASE` to your Railway API URL (no trailing slash), e.g. `https://notes-api-production.up.railway.app`.
5. Deploy — `write-config.js` runs at build time and writes `config.js`.

See [docs/deployment.md](../docs/deployment.md) for the full Neon + Railway + Vercel flow.
