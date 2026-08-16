# ClaimsManagementChubbCodingTest

## Coding Test

This Gradle project is intended to support claims management across six Chubb markets in APAC.

## Business Problem

Chubb APAC processes motor and property claims across six markets. The current process is fragmented and largely manual. Claimants often submit incidents by phone or email and then have no clear visibility into the status of their claim. Claims staff work from shared inboxes and spreadsheets, which means there is no unified view of workloads, assignment, or processing progress. Managers also lack a real-time view of outstanding claims, team performance, and potential liability exposure.

The goal is to design and build the backend that powers this platform.

Claimants need to be able to:
- report an incident,
- track the status of their claim,
- provide additional information when requested,
- receive decisions and notifications.

Claims staff need to be able to:
- receive and triage incoming claims,
- assign claims to themselves or to other team members,
- review and assess claims,
- approve, reject, or request more information,
- progress approved claims toward settlement,
- monitor the workload and performance of their team.

The frontend is out of scope. The assessment focuses on the backend structure, domain model, API boundaries, workflow design, and communication patterns required to support the full claims lifecycle.

## Architecture

### Current API and Workflow Notes

The public API uses business identifiers where possible:

- `claimNumber` identifies claims in claimant, staff, assessment, and workload flows.
- `staffNumber` identifies staff members in public endpoints.
- Internal UUIDs remain persistence and event-correlation identifiers.

Important workflow endpoints include:

- `POST /api/v1/claims` submits a claim for staff intake.
- `POST /api/v1/staff/{staffNumber}/claims/queue/{claimNumber}/pickup` assigns a queued claim.
- `POST /api/v1/claims/{claimNumber}/assessments/start` starts staff assessment.
- `POST /api/v1/claims/{claimNumber}/assessments` records the assessment result.
- `PUT /api/v1/claims/{claimNumber}/more-information` accepts claimant information requested by staff.
- `GET /api/v1/management/claims` returns total claims, liability exposure, assignment totals, outstanding claims, and officer workloads.

### Observability and Communication

Application services log workflow transitions using claim numbers, staff numbers, assessment IDs, and outbox IDs. Sensitive claimant descriptions and email content are not written to logs.

All asynchronous domain events use the transactional outbox pattern:

1. The domain update and outbox row are committed in one database transaction.
2. The scheduled outbox publisher sends pending events to ActiveMQ.
3. Successful events are marked `PUBLISHED`; failed events retain retry metadata.

The current event destinations are `staff-claim-queue`, `finance-team-queue`, and `assessment-rejected-queue`. Rejected-assessment email delivery is represented by a listener and currently mocked with a log statement for local development.

### Assumptions, Constraints, Brainstorming, and Decisions

The business process can be broken down into a few clear areas:

- Claimants can submit a claim via a REST API.
- Claimants can check claim status and view claim details.
- Claimants can provide additional information when the claim is pending more information.
- Claimants receive notifications when a claim status changes.
- Claims staff can triage incoming claims and assign them to an adjuster.
- Claims staff can review and assess each claim based on business rules and supporting information.
- Claims may be approved, rejected, or placed into a more-information workflow.
- Approved claims may proceed to settlement, which may require downstream finance or legal processing.
- Managers need summary views of outstanding claims and team workload.

### Domain Model

#### Claim
- id: UUID
- version: int
- status: ClaimStatus
- type: ClaimType
- claimantId: UUID
- claimNumber: String
- description: String
- createdAt: Date
- updatedAt: Date

#### Claimant
- id: UUID
- firstName: String
- lastName: String
- email: String
- phone: String
- address: String
- policyNumber: String

#### ClaimHistory (planned)
- id: UUID
- claimId: UUID
- status: String
- changedBy: String
- changeReason: String
- createdAt: Date

#### Assessment
- id: UUID
- claimId: UUID
- staffId: UUID
- type: String
- description: String
- details: String
- estimatedAmount: Double
- settledAmount: Double
- createdAt: Date
- result: AssessmentResult

#### ClaimStatus Enum
- SUBMITTED
- ASSIGNED
- ASSESSMENT_IN_PROGRESS
- MORE_INFO_REQUESTED
- MORE_INFO_PROVIDED
- APPROVED
- REJECTED
- SETTLEMENT_IN_PROGRESS
- CLOSED

#### AssessmentResult Enum
- APPROVED
- REJECTED
- MORE_INFO_REQUIRED

#### Relationships
- A Claimant has a one-to-many relationship with Claim: one claimant can submit multiple claims.
- A Claim has a many-to-one relationship with Claimant: many claims belong to a single claimant.

### Claim Lifecycle

Approved path:

SUBMITTED -> ASSIGNED -> ASSESSMENT_IN_PROGRESS -> APPROVED -> SETTLEMENT_IN_PROGRESS -> CLOSED

More information path:

SUBMITTED -> ASSIGNED -> ASSESSMENT_IN_PROGRESS -> MORE_INFO_REQUESTED -> (additional info provided) -> APPROVED/REJECTED

Rejected path:

SUBMITTED -> ASSIGNED -> ASSESSMENT_IN_PROGRESS -> REJECTED

This keeps the lifecycle readable and reflects the manual review nature of the process.

## Synchronous Operations (REST APIs)

The current API uses `claimNumber` and `staffNumber` at public boundaries. Internal UUIDs are retained for persistence and event correlation.

### Claimant-facing APIs
- `POST /api/v1/claims`
- `GET /api/v1/claims/{claimNumber}`
- `GET /api/v1/claims?claimantMemberNumber={memberNumber}`
- `PUT /api/v1/claims/{claimNumber}/more-information`

### Claims assessment APIs
- `POST /api/v1/claims/{claimNumber}/assign?staffNumber={staffNumber}`
- `POST /api/v1/claims/{claimNumber}/assessments/start?staffNumber={staffNumber}`
- `POST /api/v1/claims/{claimNumber}/assessments`
- `GET /api/v1/claims/{claimNumber}/assessments`
- `GET /api/v1/assessments/{assessmentId}`
- `PATCH /api/v1/assessments/{assessmentId}/settlement?settledAmount={amount}`

### Staff and queue APIs
- `GET /api/v1/staff`
- `GET /api/v1/staff/{staffNumber}`
- `GET /api/v1/staff/claims/queue`
- `GET /api/v1/staff/{staffNumber}/claims/queue`
- `POST /api/v1/staff/{staffNumber}/claims/queue/{claimNumber}/pickup`
- `POST /api/v1/staff/{staffNumber}/claims/queue/{claimNumber}/requeue`

### Workload and management APIs
- `GET /api/v1/management/claims`

The management response combines total claims, liability exposure, assignment counts, under-assessment counts, unassigned claims, outstanding claims, and per-staff workloads.

These endpoints support the basic user journeys and the operational needs of claims teams and managers.

## Asynchronous Operations

The following should be treated as asynchronous workflow events rather than synchronous user actions:

- Assigning an incoming claim to a staff member
- Reassigning a claim after it is returned for more information
- Sending email or SMS notifications to claimants
- Triggering downstream finance and legal workflows
- Updating dashboards and workload metrics after state changes
- Recording claim lifecycle events for audit and reporting

Examples of events:
- ClaimSubmitted
- ClaimAssigned
- AssessmentCompleted
- MoreInfoRequested
- ClaimApproved
- ClaimRejected
- SettlementStarted
- NotificationSent

The current local implementation uses ActiveMQ/JMS. Kafka topic configuration is retained for a production deployment profile but is not the active local publisher.


### Outbox Event Pattern

The application uses an outbox table so database state changes and event recording commit atomically. A scheduled publisher sends pending events to JMS and records publication attempts and failures. The production upgrade is to make dispatching durable and horizontally safe with leases or row locking, exponential backoff, dead-letter handling, metrics, and operator replay controls.

```
Database update
    +
Outbox event insert
    |
Transaction commits
    |
Scheduled outbox publisher
    |
JMS destination
```

## Technology Choices

For this coding test, the intended stack is:
- Java with Spring Boot
- H2 for local development and testing
- Embedded in-memory MQ (ActiveMQ) for local/no-Docker messaging during development and coding exercises

This keeps the setup lightweight and quick while still allowing the application design to reflect a realistic event-driven backend.

For production, the system should use:
- PostgreSQL or MySQL with managed backups, encryption, migrations, connection pooling, and read replicas where justified.
- Kafka or a managed JMS broker with durable queues/topics, consumer groups, retry policies, dead-letter queues, schema compatibility, and message retention policies.
- Redis only for cacheable, short-lived data, with explicit invalidation and failure behavior.
- A secrets manager for database, broker, SMTP, and signing credentials.
- A managed container or Kubernetes deployment with health probes, graceful shutdown, autoscaling, and rollback support.

## Local Access URLs

### Run locally
```bash
./gradlew bootRun
```

Then open:

### Swagger UI
- http://localhost:8080/swagger-ui.html
- http://localhost:8080/api-docs

### Actuator
- http://localhost:8080/actuator
- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/metrics

These endpoints are available when the application is running locally in the default Spring Boot profile.

## Suggested Multi-Module Structure

A sensible project structure for this backend would be:

- Claims Module
  - claim creation, retrieval, updates, and status transitions
- Claimant Module
  - claimant profile and claim relationship management
- Assessment Module
  - assessment creation, review, and validation logic
- Workload Module
  - dashboards, staffing, and team-performance metrics
- Shared/Common Module
  - events
  - messaging
  - exceptions
  - configuration
  - validation utilities

## Production-Grade Upgrade Roadmap

The following items are planned upgrades. They are intentionally listed separately from the local coding-test implementation.

### 1. Security and Access Control

- Add OAuth2/OIDC login with JWT validation.
- Enforce role-based authorization for claimant, claims adjuster, team lead, finance, and administrator operations.
- Restrict actuator endpoints and Swagger UI outside local development.
- Replace wildcard CORS with an environment-specific allowlist before production. The current `allowedOrigins("*")` setting is development-only.
- Add rate limiting, request-size limits, security headers, CSRF policy for browser sessions, and abuse monitoring.
- Redact personal data, email addresses, tokens, and financial details from logs.

### 2. Data and Schema Hardening

- Move from H2 to managed PostgreSQL or MySQL.
- Review Flyway migrations before release; use immutable versioned migrations and never rewrite migrations already applied in shared environments.
- Add database constraints for valid status transitions, non-negative monetary values, currency, and data ownership.
- Store money as `DECIMAL`/`NUMERIC` with an explicit currency rather than floating-point `DOUBLE` values.
- Add claim history/audit tables for every status, assignment, assessment, and settlement change.
- Add document metadata and object-storage references for police reports, repair estimates, and supporting evidence.
- Define retention, archival, deletion, and privacy rules for claimant and claim data.

### 3. Workflow and Domain Integrity

- Centralize and validate all legal claim status transitions.
- Record `changedBy`, reason, source system, correlation ID, and timestamps for every transition.
- Add idempotency keys to claim creation, information updates, assessment submission, pickup, and event consumers.
- Add explicit assignment history and reassignment reasons.
- Prevent duplicate assessments or conflicting decisions under concurrent requests.
- Add SLA deadlines, priority, escalation, and workload balancing rules.
- Add settlement, finance acknowledgement, payment, and closure workflows.

### 4. Messaging and Outbox Reliability

- Move production delivery from the local embedded broker to managed Kafka or durable JMS infrastructure.
- Add an outbox lease/claim mechanism so multiple application instances cannot publish the same row concurrently.
- Use exponential retry with a maximum attempt count and a dead-letter state.
- Add idempotent consumers for staff queue creation, finance handoff, and customer notifications.
- Add event schema versioning, compatibility checks, correlation IDs, causation IDs, and trace propagation.
- Add operational endpoints or a secured admin job for replaying failed outbox events.
- Add contract tests for every event producer and consumer.

### 5. Notifications and Integrations

- Replace mocked rejected-assessment email logging with a real provider integration.
- Store notification delivery status, provider message ID, retry count, and failure reason.
- Add email templates, localization, accessibility checks, and customer notification preferences.
- Add SMS/push notification support where required by market.
- Add finance and legal integration adapters with timeout, retry, reconciliation, and acknowledgement handling.

### 6. API Quality

- Add consistent pagination, filtering, sorting, and search for claims, assessments, queues, and workload reports.
- Use typed request/response DTOs everywhere and avoid returning JPA entities directly.
- Standardize error codes, validation messages, trace IDs, and RFC 7807-style problem responses.
- Add optimistic concurrency tokens or `If-Match` handling to mutable claim operations.
- Define API versioning and deprecation policy.
- Keep OpenAPI examples synchronized with Flyway fixtures and validate the specification in CI.

### 7. Observability and Operations

- Emit structured JSON logs with correlation and trace IDs.
- Add distributed tracing across HTTP, database, outbox, broker, finance, and notification boundaries.
- Add metrics for queue depth, pickup latency, assessment duration, SLA breaches, outbox age, retry count, and notification success rate.
- Add dashboards and alerts for database health, broker lag, failed events, dead letters, and elevated API errors.
- Use readiness/liveness probes and graceful shutdown for rolling deployments.
- Publish runbooks for replay, reconciliation, broker outages, database restore, and incident response.

### 8. Testing and Delivery

- Add unit tests for every domain transition and monetary calculation.
- Add controller tests for validation, authorization, error responses, and CORS behavior.
- Add repository and migration tests against PostgreSQL/Testcontainers.
- Add integration tests for transactional outbox behavior and concurrent pickup.
- Add event contract tests and notification provider tests.
- Add API compatibility, OpenAPI linting, dependency vulnerability scanning, static analysis, and formatting checks to CI.
- Build immutable artifacts and promote the same artifact across environments.

### 9. Performance and Resilience

- Replace repeated status queries with grouped database queries for larger datasets.
- Add appropriate composite indexes after measuring production query plans.
- Paginate outbox dispatch and queue reads.
- Add timeouts, circuit breakers, bulkheads, and bounded thread pools around external services.
- Test recovery from broker, database, SMTP, and downstream finance failures.
- Define capacity targets and load-test claim submission, pickup, assessment, and workload reporting.

## Summary

The overall problem is a backend design for a claims management platform that supports both claimant-facing self-service and internal operational workflows. The core challenge is to model claim lifecycle transitions clearly, expose the right REST APIs, and use asynchronous events for downstream actions such as notifications and finance/legal processing.

