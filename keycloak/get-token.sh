#!/usr/bin/env bash
set -euo pipefail

REALM="${REALM:-urban-radius}"
CLIENT_ID="${CLIENT_ID:-urban-radius-api}"
KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8080}"
USERNAME="${1:-priya@example.com}"
PASSWORD="${2:-secret}"

curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=${CLIENT_ID}" \
  -d "grant_type=password" \
  -d "username=${USERNAME}" \
  -d "password=${PASSWORD}" \
  -d "scope=openid" | python3 -m json.tool
