INSERT INTO app_user (full_name, username, email, phone, company_name, password, role, active, created_at, updated_at)
SELECT
    'Default User',
    'user',
    'user@dled.local',
    '11999999999',
    'Default Company',
    '{noop}123456',
    'USER',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM app_user WHERE username = 'user'
);
