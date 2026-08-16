# ClaimsManagementChubbCodingTest

**Coding test**

This gradle project is used to do claims management for all the six markets of Chubb

## Business problem

Chubb APAC processes motor and property claims across six markets. Today the process is fragmented: claimants submit by phone or email and wait with no visibility into what is happening. Claims staff manage incoming work from shared inboxes and spreadsheets, with no consolidated view of their workload. Managers have no real-time picture of outstanding claims or liability exposure.


Your task is to design and build the backend that powers this platform.

**Claimants** need to be able to report an incident, track their claim, provide additional information when asked, and receive decisions.

**Claims staff** need to be able to pick up incoming claims, review and assess them, progress claims to settlement or rejection, and see their team's workload and performance.

The frontend is out of scope. What the backend needs to be — its structure, its boundaries, its data model, and how it communicates — is part of the assessment.



## Architecture 

### Assumptions, Constraints, Brain storiming, Decisions,
Claimants need to be able to some actions. lets break it down 
 - report incident -> need a rest endpoint something like POST /api/v1/claims
- track their claim -> a rest endpoint like GET /api/v1/claims
- provide additional info -> an endpoint like PUT /api/v1/claims
- receive decisions -> may be a email/sms notification?

Claims staff need to be able to some actions. lets break it down 
- pickup incoming claims -> means there is a list of 'pending' claims and an officer should be able to assign to themselves. So a potential 'queue' where incoming claims are stored and also a rest endpoint like POST /claims/assignments to assign to a staff manually
- review and assess them -> May be a set of business rules and validations to be performed. means they could also changes to rules and validations based on customers, so need to keep them devoupled. also mostly 'assess' means a manual task, so when it is done, they put the result in a different queue for next process. 
- progress claims to settlement or rejection - means when a settlement is progressed, it is sent to finance team to do the finance & legal transactions and also notify customer in email. same thing for rejection also but no finance team involved. If in case finance and legal teams are to be notified, again we expect an endpoint or queue, based on async or sync decisions. I think as the customer dont see this, there could be async process but again need to check with business about the ETAs.
- see their team's workload and performance -> means a manager or admin can have a UI dashboard to see the # of claims and their statuses. So endpoints for this GET /api/v1/management/claims gives all the claims and statuses, etc.


### Entities
Listing down potential entities
#### Claim
- Id:  UUID
- version: int
- status: ClaimStatus
- name: string
- crated_at: Date
- modified_at: DAte
- claimNumber: UUID
- description: String

#### Claimant
- id: UUID
- name: String
- email: String
- phone: String
- address: String
- policyNumber: UUID // potential foreign key of insurance policy number

#### ClaimHistory
- id: UUID
- claimId: UUIUD
- status: string
- createdAt: Date

#### Assessment
- id: String
- claimId: String
- staffId: string
- type: String
- description: String
- details: String
- estimatedAmount: double
- settledAmount; double
- createdAt: Date
- claimStatus: ClaimSTatus
- result: MORE_INFO/CLOSED


#### ClaimStatus: ENUM
SUMITTED, ASSIGNED,ASSESSMENT_INPROGRESS,APPROVED,REJECTED,SETTLEMENT_INPROGRESS,CLOSED

#### Claim Lifecycle
APPROVED PATH:

SUBMITTED->ASSIGNED->ASSEMENT_INPROGRESS->APPROVED->SETTLEMENT_INPROGRESS->CLOSED

MORE INFO PATH:

SUBMITTED->ASSIGNED->ASSEMENT_INPROGRESS->MORE_INFO_REQUESTED*->SETTLEMENT_INPROGRESS/REJECTED->CLOSED


REJECTED PATH:

SUBMITTED->ASSIGNED->ASSEMENT_INPROGRESS->REJECTED


### Sync operations (REST Endpoints)
<p>POST /api/v1/claims</p>
<p>GET  /api/v1/claims/{claimId}</p>
<p>PUT /api/v1/claims/{claimId}

<p>POST /api/v1/claims/{claimId}/assessments</p>
<p>GET  /api/v1/claims/{claimId}/assessments</p>


For Workload
<p>GET /api/v1/management/claims/p>
<p>GET /api/v1/management/claims/p>
<p>GET /api/v1/staff/{staffId}/claims</p>


#### Async operations
- Assigning a Submitted claim
- Assigning a 'MORE_INFO_REQUESTED' claim
- Notification (Email/sms) to Customer for 'MORE_INFO_REQUESTED/APPROVED/REJECTED/SETTLED' claims
- Notification to finance & legal teams


I am using the following stack - Java with SPring boot, H2, embedded kafka as this is for a simple coding test and I dont want to waste time in configuring db, etc. But for production I will use a RDBMS database and kafka cluster, and a redis cache wherever necessary

### Mental map of controllers and services in this multi-module project

- Claims Module
- Claimant Module
- Assessment Module
- workload Module

The following will be common components and shared across
- events
- messaging
- exceptions
- config  

##
