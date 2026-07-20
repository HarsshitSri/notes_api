# Documentation Index

Supplementary documentation for the [Notes API](../README.md) project. The root [README](../README.md) remains the primary entry point for setup, API reference, architecture flows, and deployment. Use this index to find deeper or specialized docs.

---

## Start here

| If you want to… | Read |
| ---------------- | ---- |
| Run the app, call endpoints, or deploy with Docker | [README](../README.md) — Getting Started, API Overview, Docker Instructions |
| Understand how source packages are organized | [packages.md](packages.md) |
| See the full repository layout | [project-tree.md](project-tree.md) |
| Learn why the project is built this way | [Decisions.md](../Decisions.md) |
| Run the Spring Boot module locally | [note-app/HELP.md](../note-app/HELP.md) |

**Suggested path for new contributors:** [project-tree.md](project-tree.md) → [packages.md](packages.md) → [README § Architecture](../README.md#architecture-overview) → [Decisions.md](../Decisions.md) → [README § Getting Started](../README.md#getting-started).

---

## Documents in `docs/`

| Document | Audience | Summary |
| -------- | -------- | ------- |
| [packages.md](packages.md) | Contributors | Per-package responsibilities (`controller`, `service`, `repository`, `security`, `dto`, `mapper`, `model`, `exception`, `config`) |
| [project-tree.md](project-tree.md) | Contributors | Repository tree with directory responsibilities; notes on legacy empty folders |
| [diagram-audit.md](diagram-audit.md) | Maintainers | Architecture and database diagram accuracy review vs. current code |
| [assets-plan.md](assets-plan.md) | Maintainers / designers | Planned PNG diagrams and screenshots (specs only — assets not yet generated) |

---

## Related documentation (outside `docs/`)

| Document | Location | Summary |
| -------- | -------- | ------- |
| Project README | [README.md](../README.md) | Overview, features, architecture, database, auth, full API reference, env vars, Docker, testing, roadmap |
| Technical decisions | [Decisions.md](../Decisions.md) | 20 ADR-style records with alternatives, tradeoffs, and Git-history evidence |
| Security policy | [SECURITY.md](../SECURITY.md) | Vulnerability reporting; production warnings (JWT secret, credentials) |
| Module help | [note-app/HELP.md](../note-app/HELP.md) | Java 21 / Maven requirements, `h2` vs PostgreSQL run commands, Spring reference links |

---

## By topic

### Architecture and code layout

- Layer flows and request paths: [README § Architecture Overview](../README.md#architecture-overview)
- Package reference: [packages.md](packages.md)
- Diagram accuracy: [diagram-audit.md](diagram-audit.md)
- Future architecture PNGs: [assets-plan.md](assets-plan.md) § Diagrams

### API and authentication

- Endpoint reference (7 routes): [README § API Overview](../README.md#api-overview)
- Auth model (JWT, public vs protected): [README § Authentication Overview](../README.md#authentication-overview)
- JWT contract (header, claims, expiry, no refresh): [README § JWT reference](../README.md#jwt-reference)
- Interactive docs (when app is running): Swagger UI at `http://localhost:8080/swagger-ui.html`

### Database

- Entities and profiles: [README § Database Overview](../README.md#database-overview)
- Schema diagram checklist: [diagram-audit.md](diagram-audit.md) § Database
- Planned ERD asset: [assets-plan.md](assets-plan.md) — `database-schema.png`

### Deployment and configuration

- Local run (H2 / PostgreSQL): [README § Running Locally](../README.md#running-locally)
- Environment variables: [README § Environment Variables](../README.md#environment-variables)
- Docker Compose: [README § Docker Instructions](../README.md#docker-instructions)
- Security policy (vulnerability reporting, JWT secret warnings): [SECURITY.md](../SECURITY.md)

### Testing

- How to run tests: [README § Testing](../README.md#testing)
- Testing strategy rationale: [Decisions.md § 20](../Decisions.md#20-testing-strategy)

### Design history

- Full decision log: [Decisions.md](../Decisions.md)
- Decision timeline: [Decisions.md § Decision timeline](../Decisions.md#decision-timeline-git-history)

---

## Maintainer notes

- Before adding or updating architecture diagrams, read [diagram-audit.md](diagram-audit.md) and [assets-plan.md](assets-plan.md).
- Keep [project-tree.md](project-tree.md) in sync when top-level folders or key files change.
- Keep [packages.md](packages.md) in sync when packages or major classes are added or renamed.
