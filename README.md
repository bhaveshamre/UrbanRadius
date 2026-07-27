# Urban Radius

Hyperlocal skill and service exchange platform.

## Phase 3a — Keycloak (Identity Provider)

### Start Keycloak

```bash
docker compose up -d
```

Wait ~30 seconds for startup, then open:

- Admin console: http://localhost:8080/admin
- Admin login: `admin` / `admin`
- Realm: `urban-radius` (auto-imported)

### Test users

| Email | Password | Role |
|---|---|---|
| priya@example.com | secret | PROVIDER |
| amit@example.com | secret | SEEKER |
| admin@urbanradius.com | admin123 | ADMIN |

### Get a JWT (Postman / curl)

```bash
chmod +x keycloak/get-token.sh
./keycloak/get-token.sh priya@example.com secret
```

Or manually:

```bash
curl -X POST http://localhost:8080/realms/urban-radius/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=urban-radius-api" \
  -d "grant_type=password" \
  -d "username=priya@example.com" \
  -d "password=secret" \
  -d "scope=openid"
```

Copy `access_token` from the response. User Service validates JWTs on protected routes (Phase 3b).

### Phase 3b — Protected routes

| Endpoint | JWT required? |
|---|---|
| `GET /api/users/{id}` | No |
| `POST /api/users/register` | Yes |
| `POST /api/users/{id}/rate` | Yes |

Unauthenticated protected calls return:
```json
{ "errorCode": "UNAUTHORIZED", "message": "Valid JWT required", "timestamp": "..." }
```

### Phase 3c — JWT-linked profiles

Register body (no email — comes from JWT):
```json
{ "fullName": "Priya Sharma", "phone": "+91-9876543210", "city": "Bangalore", "role": "PROVIDER" }
```

Rate body (no raterId — comes from JWT):
```json
{ "score": 5 }
```

New endpoint: `GET /api/users/me` (JWT required)

**Full test flow:**
```bash
docker compose up -d
mvn spring-boot:run -pl user-service

# Provider token + register
PROVIDER_TOKEN=$(./keycloak/get-token.sh priya@example.com secret | python3 -c "import sys,json; print(json.load(sys.stdin)['access_token'])")
curl -X POST http://localhost:8081/api/users/register \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Priya Sharma","phone":"+91-9876543210","city":"Bangalore","role":"PROVIDER"}'

curl http://localhost:8081/api/users/me -H "Authorization: Bearer $PROVIDER_TOKEN"

# Seeker token + register + rate
SEEKER_TOKEN=$(./keycloak/get-token.sh amit@example.com secret | python3 -c "import sys,json; print(json.load(sys.stdin)['access_token'])")
curl -X POST http://localhost:8081/api/users/register \
  -H "Authorization: Bearer $SEEKER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Amit Kumar","phone":"+91-9876543211","city":"Bangalore","role":"SEEKER"}'

curl -X POST http://localhost:8081/api/users/{provider-id}/rate \
  -H "Authorization: Bearer $SEEKER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"score":5}'
```

If upgrading from Phase 2 data: `DROP TABLE user_profiles;` then restart.

## Phase 4 — Catalog Service (port 8082)

MongoDB stores skill/service listings. Start infrastructure:

```bash
docker compose up -d    # Keycloak + MongoDB
mvn spring-boot:run -pl catalog-service
```

### APIs

| Method | Path | Auth |
|---|---|---|
| `POST` | `/api/listings` | JWT + PROVIDER |
| `GET` | `/api/listings` | Public (filter by `city`, `category`, `subcategory`) |
| `GET` | `/api/listings/{id}` | Public |
| `GET` | `/api/listings/my` | JWT + PROVIDER |
| `PUT` | `/api/listings/{id}` | JWT + owner |
| `DELETE` | `/api/listings/{id}` | JWT + owner (soft-delete) |

### Test flow

```bash
PROVIDER_TOKEN=$(./keycloak/get-token.sh priya@example.com secret | python3 -c "import sys,json; print(json.load(sys.stdin)['access_token'])")

# Get provider profile id from User Service
PROVIDER_ID=$(curl -s http://localhost:8081/api/users/me -H "Authorization: Bearer $PROVIDER_TOKEN" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")

# Create listing
curl -X POST http://localhost:8082/api/listings \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"providerId\": \"$PROVIDER_ID\",
    \"title\": \"AC Repair & Servicing\",
    \"description\": \"All brands. Same-day visits in Bangalore.\",
    \"category\": \"HOME_REPAIR\",
    \"subcategory\": \"AC_REPAIR\",
    \"priceAmount\": 500,
    \"priceUnit\": \"PER_VISIT\",
    \"city\": \"Bangalore\",
    \"attributes\": { \"brands\": [\"LG\", \"Samsung\"], \"emergencyAvailable\": true }
  }"

# Search (no token)
curl "http://localhost:8082/api/listings?city=Bangalore&category=HOME_REPAIR"
```

### Stop services

```bash
docker compose down
```
