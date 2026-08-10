#!/bin/sh
set -eu
# Dev-mode sample backup: export important lab configuration as JSON.
# In production use storage snapshots according to the selected Vault backend.
export VAULT_ADDR=${VAULT_ADDR:-http://127.0.0.1:8200}
export VAULT_TOKEN=${VAULT_TOKEN:-root}
mkdir -p backups
vault kv get -format=json secret/payment-processing/dev > backups/kv-payment-processing-dev.json
vault read -format=json transit/keys/payment-card-key > backups/transit-payment-card-key.json
vault read -format=json database/roles/payment-service-role > backups/database-payment-service-role.json
