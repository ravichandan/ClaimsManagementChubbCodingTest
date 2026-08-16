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

#### ClaimHistory
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
- APPROVED
- REJECTED
- SETTLEMENT_IN_PROGRESS
- CLOSED

#### AssessmentResult Enum
- APPROVED
- REJECTED
- MORE_INFO_REQUIRED

### Claim Lifecycle

Approved path:

SUBMITTED -> ASSIGNED -> ASSESSMENT_IN_PROGRESS -> APPROVED -> SETTLEMENT_IN_PROGRESS -> CLOSED

More information path:

SUBMITTED -> ASSIGNED -> ASSESSMENT_IN_PROGRESS -> MORE_INFO_REQUESTED -> (additional info provided) -> APPROVED/REJECTED

Rejected path:

SUBMITTED -> ASSIGNED -> ASSESSMENT_IN_PROGRESS -> REJECTED

This keeps the lifecycle readable and reflects the manual review nature of the process.

## Synchronous Operations (REST APIs)

### Claimant-facing APIs
- POST /api/v1/claims
- GET /api/v1/claims/{claimId}
- PUT /api/v1/claims/{claimId}
- GET /api/v1/claims

### Claims assessment APIs
- POST /api/v1/claims/{claimId}/assignments
- POST /api/v1/claims/{claimId}/assessments
- GET /api/v1/claims/{claimId}/assessments
- POST /api/v1/claims/{claimId}/decisions
- POST /api/v1/claims/{claimId}/settlements

### Workload and management APIs
- GET /api/v1/management/claims
- GET /api/v1/management/claims?status={status}
- GET /api/v1/staff/{staffId}/claims

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

These events can be published to Kafka or an equivalent messaging layer, which keeps the system decoupled and easier to extend.

## Technology Choices

For this coding test, the intended stack is:
- Java with Spring Boot
- H2 for local development and testing
- Embedded in-memory MQ (ActiveMQ) for local/no-Docker messaging during development and coding exercises

This keeps the setup lightweight and quick while still allowing the application design to reflect a realistic event-driven backend.

For production, the system will switch to:
- a relational database such as PostgreSQL or MySQL,
- a Kafka cluster for event streaming and integration,
- Redis for caching or short-lived operational data when needed,
- stronger observability and monitoring.

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

## Areas to Consider Further

The following are important to include in the design even if they are not fully implemented in the coding test:

- Role-based access control
- Audit logging
- Claim search and filtering
- Notification delivery tracking and retries
- Handling multiple claim documents and attachments
- Idempotent API behaviour
- Concurrency control when claims are assigned or updated
- Validation and domain rules for claim types
- Dashboard metrics such as queue length, SLA breaches, and throughput

## Summary

The overall problem is a backend design for a claims management platform that supports both claimant-facing self-service and internal operational workflows. The core challenge is to model claim lifecycle transitions clearly, expose the right REST APIs, and use asynchronous events for downstream actions such as notifications and finance/legal processing.

