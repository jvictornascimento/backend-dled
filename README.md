# DLED Backend

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
