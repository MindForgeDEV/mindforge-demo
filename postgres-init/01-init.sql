-- Set authentication method to MD5 for local connections
ALTER SYSTEM SET password_encryption = 'md5';
-- Copy custom pg_hba.conf to allow MD5 authentication
-- Note: This will be executed after database initialization
-- Reload configuration
SELECT pg_reload_conf();
