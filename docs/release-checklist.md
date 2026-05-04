# Production Release Checklist

## Environment and Secrets
- Set required env vars: `DB_PASSWORD`, `JWT_SECRET`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`.
- Confirm `SPRING_PROFILES_ACTIVE=prod` in deployment.
- Verify no fallback secrets/passwords are used in production.

## Database and Migrations
- Use schema migration tool for production rollout (Flyway/Liquibase) before app deploy.
- Confirm unique constraint for `orders.code` exists in target database.
- Disable seed data loading in production (`spring.sql.init.mode=never`).

## Security Verification
- Confirm debug endpoints are removed (no `/api/public/test/**`).
- Validate `GET /api/users/customers` only accessible to `ADMIN` and `MANAGER`.
- Validate CORS allows only approved origins for deployed frontend domains.

## Payment and Inventory
- Verify online payments stay `PENDING` until gateway callback confirms success.
- Run concurrent checkout test to ensure no oversell on hot variants.
- Verify order cancellation/return correctly releases stock.

## API Quality Gate
- Run test suite and ensure all core tests pass.
- Smoke-test auth, order create, cancel, return, and admin customer listing endpoints.
- Verify error responses for validation and bad request scenarios.

## Observability
- Confirm audit logs are recorded for stock imports, user updates, and critical admin actions.
- Ensure application error logs are collected by runtime platform.
