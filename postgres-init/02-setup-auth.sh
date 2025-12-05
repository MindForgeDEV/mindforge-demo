#!/bin/bash
# Copy custom pg_hba.conf to allow MD5 authentication for container connections
cp /docker-entrypoint-initdb.d/pg_hba.conf /var/lib/postgresql/data/pg_hba.conf
# Reload PostgreSQL configuration
pg_ctl reload