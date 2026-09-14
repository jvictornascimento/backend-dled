# DLED Backend

## Production environment

The Docker Compose setup uses the `prod` Spring profile and fails fast when required secrets are missing.

Required variables:

```env
DBNAME=dled
DBUSERNAME=dled_app
DBPASSWORD=change-this-password
API_KEY_VALUE=change-this-api-key
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
CLOUDINARY_FOLDER=dled/products
MAX_UPLOAD_FILE_SIZE=5MB
MAX_UPLOAD_REQUEST_SIZE=6MB
```

Production disables Swagger by default, exposes only `/actuator/health`, forces secure JWT cookies and uses database credentials from the environment. Do not deploy with test secrets or default database passwords.

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
