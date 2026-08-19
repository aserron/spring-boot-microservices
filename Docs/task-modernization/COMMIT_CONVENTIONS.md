# Commit Message Specification & Atomic Commit Policy

To ensure commit history is focused, traceable, and modular, all commits in this repository follow the **Conventional Commits 1.0.0** specification with **strict atomicity rules**.

---

## 1. Commit Structure

```text
<type>(<scope>): <imperative summary in lowercase>

[optional body explaining motivation and technical rationale]

[optional footer(s)]
```

---

## 2. Types & Scopes

### Allowed Types (`<type>`)
- `feat`: New feature or endpoint functionality.
- `fix`: Bug remediation or specification correction.
- `refactor`: Code changes that neither fix a bug nor add a feature (e.g. cleanup, restructuring).
- `test`: Adding or correcting unit/integration tests.
- `build`: Changes that affect the build system or dependencies (`pom.xml`, wrapper, plugins).
- `ci`: CI/CD configuration files and pipelines.
- `docs`: Documentation only changes.
- `chore`: Maintenance tasks (e.g. updating `.gitignore`).

### Allowed Scopes (`<scope>`)
- `merchant`: Merchant microservice domain, endpoints, or repositories.
- `pm`: Payment Manager gateway domain or core controllers.
- `fx`: Currency conversion and Fixer.io integration/caching.
- `batch`: 30-second transaction clearing job (`TransactionJobService`).
- `infra`: Root POM, Docker Compose, or multi-module configuration.
- `docker`: Dockerfiles, Jib packaging, or container definitions.

---

## 3. Atomicity Rules (Small & Focused on Single Concerns)

1. **One Concern per Commit**: Never combine infrastructure changes (`pom.xml`), business logic (`SaleServiceImpl`), and documentation in a single commit.
2. **Every Commit Must Build & Pass Tests**: No broken intermediate states (`mvn test` must succeed on every commit).
3. **Imperative Mood**: Use imperative present tense in the subject line (e.g. `add`, `fix`, `refactor`, not `added`, `fixing`, `fixed`).
4. **No Marketing or Vague Phrasing**: Forbid generic messages like `updates`, `wip`, `misc fixes`. Specify the exact technical change.

---

## 4. Examples of Atomic Commits

```text
fix(pm): resolve status endpoint uuid regex parsing error

Remove numeric-only regex on /pm/status/{id} to permit standard UUIDv4 strings.
```

```text
feat(fx): implement 30s in-memory rate cache with eur triangulation

Avoid third-party API rate-limit exhaustion by serving cached rates with 30s TTL.
```

```text
fix(batch): correct pending clearing transition probability to 70-30

Align probability logic with spec: 70% chance of PAID, 30% chance of REJECTED.
```

```text
build(infra): aggregate modules under root parent pom

Define multi-module packaging, centralized plugin management, and Eclipse ECJ compiler.
```

```text
test(merchant): add mockmvc integration tests for check endpoint

Verify 200 OK on existing merchant and 404 Not Found on missing id using in-memory H2.
```
