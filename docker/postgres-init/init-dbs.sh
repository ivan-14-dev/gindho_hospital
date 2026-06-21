#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_DIR="${SCRIPT_DIR}/../../db/sql"
SCHEMA_SQL="${SQL_DIR}/init-all-schemas.sql"
ADMIN_ZONE_SQL="${SCRIPT_DIR}/../../../admin-zone/db/sql/init-admin-zone.sql"

until pg_isready -U "${POSTGRES_USER}"; do
  echo "Waiting for PostgreSQL..."
  sleep 2
done

dbs=(
  "patient_service" "appointment_service" "billing_service" "pharmacy_service"
  "laboratory_service" "admission_service" "emergency_service" "medical_record_service"
  "bed_service" "ward_service" "surgery_service" "imaging_service"
  "inventory_service" "hr_service" "notification_service" "audit_service"
  "insurance_service" "event_service" "authorization_service" "identity_service"
  "payment_service" "prescription_service" "scheduling_service" "ambulance_service"
  "asset_service" "procurement_service" "round_service" "reporting_service"
  "api_gateway" "generator_core" "kong" "keycloak"
)

create_database() {
  local db="$1"
  echo "Creating database: ${db}"
  psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -tc \
    "SELECT 1 FROM pg_database WHERE datname = '${db}'" | grep -q 1 || \
    psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -c "CREATE DATABASE ${db}"
}

apply_schema() {
  local db="$1"
  local sql_file="$2"
  if [[ -f "${sql_file}" ]]; then
    echo "Applying schema to database: ${db}"
    psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" --dbname "${db}" -f "${sql_file}"
  else
    echo "WARN: Schema file not found: ${sql_file}"
  fi
}

create_database "gindho"
for db in "${dbs[@]}"; do
  create_database "${db}"
done
create_database "admin_zone"

if [[ -f "${SCHEMA_SQL}" ]]; then
  echo "Applying GinDHO microservice schemas..."
  apply_schema "gindho" "${SCHEMA_SQL}"
  for db in "${dbs[@]}"; do
    apply_schema "${db}" "${SCHEMA_SQL}"
  done
else
  echo "WARN: Schema file not found: ${SCHEMA_SQL}"
fi

if [[ -f "${ADMIN_ZONE_SQL}" ]]; then
  echo "Applying Admin Zone schema..."
  apply_schema "admin_zone" "${ADMIN_ZONE_SQL}"
else
  echo "WARN: Admin Zone schema file not found: ${ADMIN_ZONE_SQL}"
fi

echo "All databases and schemas created successfully!"
