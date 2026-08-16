# Running the Application with Swagger UI

## Start the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## Access Swagger UI

Once the application is running, open:

**Swagger UI (Interactive API Documentation):**
```
http://localhost:8080/swagger-ui.html
```

**OpenAPI JSON Spec:**
```
http://localhost:8080/api-docs
```

**OpenAPI YAML Spec:**
```
http://localhost:8080/api-docs.yaml
```

## Actuator Endpoints

Health and metrics are available at:

```
http://localhost:8080/actuator                      # All endpoints
http://localhost:8080/actuator/health               # Health status
http://localhost:8080/actuator/health/details       # Detailed health
http://localhost:8080/actuator/metrics               # Metrics
http://localhost:8080/actuator/info                 # Application info
```

## Using Swagger UI

1. Navigate to `http://localhost:8080/swagger-ui.html`
2. You'll see all API endpoints organized by tags:
   - **Claimants** - Create and retrieve claimants
   - **Claims** - Manage claims and lifecycle
   - **Workload** - Monitor workload summary
3. Click any endpoint to see:
   - Request/response schemas
   - Example payloads
   - Parameter descriptions
4. Use "Try it out" button to test endpoints directly

## Example Requests

### Create a Claimant
```bash
curl -X POST http://localhost:8080/api/v1/claimants \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phone": "+1-555-0100",
    "address": "123 Main St",
    "policyNumber": "POL-123"
  }'
```

### Submit a Claim
```bash
curl -X POST http://localhost:8080/api/v1/claims \
  -H "Content-Type: application/json" \
  -d '{
    "claimantId": "<claimant-id>",
    "claimType": "MOTOR",
    "description": "Car accident claim"
  }'
```

### Get Workload Summary
```bash
curl http://localhost:8080/api/v1/management/claims
```

## Troubleshooting

**Swagger UI not appearing:**
- Check that `springdoc.swagger-ui.enabled=true` in application.properties
- Verify actuator endpoints are exposed: `management.endpoints.web.exposure.include=health,info,metrics,openapi,swagger-ui`

**OpenAPI spec not generating:**
- Make sure your controllers have `@RestController` and `@RequestMapping` annotations
- Check Spring Boot logs for any bean initialization errors

**Port conflicts:**
- If 8080 is in use, change server.port in application.properties and update Swagger URL accordingly
