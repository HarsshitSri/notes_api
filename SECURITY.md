# Security Policy

Notes API is a JWT-authenticated REST service. If you discover a security vulnerability, please report it responsibly rather than opening a public issue.

---

## Reporting a vulnerability

**Preferred:** Use [GitHub Private Security Advisories](https://github.com/HarsshitSri/notes_api/security/advisories/new) for this repository.

Include as much detail as you can:

- Description of the issue and potential impact
- Steps to reproduce
- Affected endpoints, versions, or configuration (e.g. default profile, Docker Compose)
- Proof-of-concept code or HTTP requests, if available
- Suggested fix, if you have one

**What to expect**

- Acknowledgment within a reasonable timeframe (target: 7 days)
- Updates as the report is triaged and addressed
- Credit in release notes if you wish (optional)

**Please do not**

- Open public GitHub issues for exploitable security bugs
- Test against production systems you do not own without permission

For non-security bugs and feature requests, use the regular [issue tracker](https://github.com/HarsshitSri/notes_api/issues).

---

## Deployment warnings

This project ships with **development-oriented defaults**. Do not deploy to a network-accessible environment without changing them.

### Default JWT signing secret

The application and Docker Compose use a **known default** JWT secret when `JWT_SECRET` is not set:

```text
this-is-a-very-long-secret-key-for-jwt-signing-32chars-min
```

Defined in:

- `note-app/src/main/resources/application.properties`
- `.env.example`
- `docker-compose.yml` (fallback value)

Anyone who knows this secret can forge valid tokens for any user. **Always set a strong, unique `JWT_SECRET` in production** (at least 32 random bytes; use a secrets manager or environment injection, not committed files).

```bash
# Example: generate a random secret (Linux/macOS)
openssl rand -base64 48
```

Set before starting the app:

```bash
export JWT_SECRET="<your-generated-secret>"
```

Or in `.env` for Docker Compose (never commit real secrets):

```bash
JWT_SECRET=<your-generated-secret>
```

### Default database credentials

Default PostgreSQL credentials (`notes_user` / `notes_pass` in Compose) are for local development only. Override `SPRING_DATASOURCE_*` and `POSTGRES_*` variables for any shared or production deployment.

### CORS (cross-origin UI)

When the UI runs on Vercel and the API on Railway, browsers enforce CORS. Set:

```text
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app
```

Do not use `*` with credentialed flows; this API uses Bearer tokens and lists explicit origins. See [docs/deployment.md](docs/deployment.md).

### Other security characteristics

| Topic | Current behavior |
| ----- | ---------------- |
| Token revocation | Not implemented — stolen tokens remain valid until expiry (`JWT_EXPIRATION`, default 24h) |
| Refresh tokens | Not implemented |
| Rate limiting | Not implemented |
| CORS | `CORS_ALLOWED_ORIGINS` (comma-separated); empty → localhost patterns only |
| CSRF | Disabled (stateless Bearer-token API) |
| Roles / RBAC | Not implemented — all authenticated users have equal access |
| Actuator / health | Spring Actuator not enabled |

See [README § Authentication Overview](README.md#authentication-overview) and [Decisions.md](Decisions.md) for design context.

---

## Supported versions

Security fixes are applied on the **`main`** branch. There are no versioned releases yet. If you run a fork or older commit, verify whether these defaults and behaviors still apply.

---

## Security-related documentation

| Document | Topic |
| -------- | ----- |
| [README § Environment Variables](README.md#environment-variables) | `JWT_SECRET`, `JWT_EXPIRATION`, database config, CORS |
| [docs/deployment.md](docs/deployment.md) | Neon + Railway + Vercel production setup |
| [README § Authentication Overview](README.md#authentication-overview) | JWT usage, public vs protected routes |
| [README § JWT reference](README.md#jwt-reference) | Token header, claims, expiry, no refresh token |
| [README § Docker Instructions](README.md#docker-instructions) | Container deployment and env vars |
| [Decisions.md § JWT authentication](Decisions.md#7-jwt-authentication-stateless) | Why JWT was chosen and known tradeoffs |
