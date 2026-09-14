CREATE UNIQUE INDEX uk_app_user_username_lower
    ON app_user (LOWER(username))
    WHERE username IS NOT NULL;

CREATE UNIQUE INDEX uk_app_user_email_lower
    ON app_user (LOWER(email))
    WHERE email IS NOT NULL;

CREATE UNIQUE INDEX uk_api_key_key_hash
    ON api_key (key_hash)
    WHERE key_hash IS NOT NULL;
