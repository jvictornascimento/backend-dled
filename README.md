# DLED Backend

## Production environment

The Docker Compose setup uses the `prod` Spring profile and fails fast when required secrets are missing.

Required variables:

```env
DBNAME=dled
DBUSERNAME=dled_app
DBPASSWORD=change-this-password
API_KEY_PRIMARY_ID=primary
API_KEY_PRIMARY_HASH=base64-sha256-hash
JWT_SECRET=change-this-to-a-long-random-secret-with-at-least-64-characters
CORS_ORIGIN=https://your-frontend-domain.com
CLOUDINARY_CLOUD_NAME=your-cloud
CLOUDINARY_API_KEY=your-key
CLOUDINARY_API_SECRET=your-secret
```

Optional variables:

```env
JWT_EXPIRATION_MINUTES=60
JWT_COOKIE_SAME_SITE=Lax
REDIS_PORT=6379
LOGIN_RATE_LIMIT_MAX_FAILED_ATTEMPTS=5
LOGIN_RATE_LIMIT_BLOCK_MINUTES=15
LOGIN_RATE_LIMIT_FAILURE_WINDOW_MINUTES=15
API_KEY_SECONDARY_ID=next
API_KEY_SECONDARY_HASH=base64-sha256-hash-for-rotation
CLOUDINARY_FOLDER=dled/products
MAX_UPLOAD_FILE_SIZE=5MB
MAX_UPLOAD_REQUEST_SIZE=6MB
```

Production disables Swagger by default, exposes only `/actuator/health`, forces secure JWT cookies and uses database credentials from the environment. Do not deploy with test secrets or default database passwords.

Production uses Redis to share login rate limit state across application instances. Docker Compose starts Redis automatically and configures the application with `LOGIN_RATE_LIMIT_STORE=redis`.

## Public API key rotation

Public read endpoints validate API keys by SHA-256 hash. Do not store the plain API key in repository files or Docker Compose variables.

Create a hash for a new key:

```bash
printf '%s' 'your-new-api-key' | openssl dgst -sha256 -binary | openssl base64
```

To rotate without downtime, set `API_KEY_SECONDARY_ID` and `API_KEY_SECONDARY_HASH` with the new key hash, deploy, update clients to the new plain key, then promote it to `API_KEY_PRIMARY_HASH` and clear the secondary variables on a later deploy.

## Web authentication

The web login endpoint stores the JWT only in the `AUTH_TOKEN` HttpOnly cookie. The login response body returns the authenticated user data and does not expose the token.

Browser clients should send authenticated requests with credentials enabled so the cookie is included automatically. Do not store the JWT in `localStorage` or `sessionStorage`.

## Initial admin

The application can bootstrap the first administrator user during startup. This is intended for the first production deploy, before any admin account exists.

When `INITIAL_ADMIN_ENABLED=true` and no user with role `ADMIN` exists, the application creates one admin using the configured environment variables and stores the password with the application's `PasswordEncoder`.

Required variables:

```env
INITIAL_ADMIN_ENABLED=true
INITIAL_ADMIN_FULL_NAME=Administrador
INITIAL_ADMIN_USERNAME=admin
INITIAL_ADMIN_EMAIL=admin@dled.com
INITIAL_ADMIN_PHONE=11999999999
INITIAL_ADMIN_COMPANY_NAME=DLED
INITIAL_ADMIN_PASSWORD=Admin@123
```

The password must have 8 to 20 characters and include uppercase, lowercase, number and special character.

After the first admin is created and you confirm login, remove these variables from the production environment or set:

```env
INITIAL_ADMIN_ENABLED=false
```

Do not commit production passwords or create the admin manually with a plain-text password in the database.

## Role permissions

The backend enforces route permissions by role and HTTP method. The current production rule is:

| Role | Permissions |
| --- | --- |
| `ADMIN` | Full access to all authenticated endpoints. |
| `EMPLOY` | Can read operational data and create, update or delete products, wood products, orders and print templates/labels. |
| `USER` | Can read allowed authenticated operational data, but cannot write administrative resources. |
| `SELLER` | Can read allowed order and print template data, but cannot write administrative resources. |
| `CLIENT` | Can authenticate only if future routes explicitly support this role. |

Company, category, wood category and user management remain restricted to `ADMIN`.
