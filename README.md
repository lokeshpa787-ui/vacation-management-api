## Vacation Day Calculation Rules

- Vacation requests are inclusive of both start and end dates.
- All calendar days are counted (weekends included).
- Vacation requests spanning multiple calendar years are not supported.
  Requests must start and end within the same calendar year.
- Remaining vacation days are calculated based only on APPROVED requests.



Vacation Management Application – Simple Run
Book
This document explains how to run the application, use the preloaded data, generate login tokens, and
test every API step by step.
You can follow this guide even if you have no background in Java or Spring Boot.
1. What does this application do?
   •
   •
   •
   •
   Workers
   Create vacation requests
   View their vacation requests
   Check how many vacation days they have left
   •
   Managers
   •
   •
   •
   •
   View all vacation requests
   Approve or reject requests
   Ensure enough employees remain at work
   View overlapping vacation days
   The application uses secure login tokens (JWT) to control access.
2. What you need before starting
   •
   •
   Java 17 installed
   Maven installed
   Verify installation:
   java -version
   mvn -version
3. Start the application
   From the project root directory, run:
   1
   mvn clean spring-boot:run
   If successful, the application will start at:
   http://localhost:8080
4. Preloaded (seed) data
   The application already contains sample users and vacation data.
   Employees already available
   You do not need to create users manually.
   Name Role Employee ID
   Alice ROLE_WORKER 11111111-1111-1111-1111-111111111111
   Bob ROLE_WORKER 22222222-2222-2222-2222-222222222222
   Carol ROLE_MANAGER 33333333-3333-3333-3333-333333333333
5. Generate login tokens (required for all APIs)
   Every API request needs a token.
   5.1 Generate a Worker token (Alice)
   curl -X POST http://localhost:8080/api/v1/auth/token
   -H "Content-Type: application/json"
   -d '{
   "employeeId": "11111111-1111-1111-1111-111111111111",
   "role": "ROLE_WORKER"
   "role": "ROLE_WORKER"
   }'
   Sample output
   2
   {
   "token": "eyJhbGciOiJIUzI1NiJ9..."
   }
   Save this as WORKER_TOKEN.
   5.2 Generate a Manager token (Carol)
   curl -X POST http://localhost:8080/api/v1/auth/token
   -H "Content-Type: application/json"
   -d '{
   "employeeId": "33333333-3333-3333-3333-333333333333",
   "role": "ROLE_MANAGER"
   }'
   Save this as MANAGER_TOKEN.
6. API Reference with Examples
   AUTH API
   Generate Token
   POST /api/v1/auth/token
   Input
   {
   "employeeId": "UUID",
   "role": "ROLE_WORKER | ROLE_MANAGER"
   }
   Output
   3
   {
   "token": "<JWT_TOKEN>"
   }
   WORKER APIs (ROLE_WORKER)
1. List own vacation requests
   GET /api/v1/worker/requests
   [
   {
   "id": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
   "status": "APPROVED",
   "vacationStartDate": "2026-12-20",
   "vacationEndDate": "2026-12-24",
   "comment": "Family trip"
   }
   ]
2. Get a single vacation request
   GET /api/v1/worker/requests/{id}
   {
   "id": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
   "status": "APPROVED",
   "vacationStartDate": "2026-12-20",
   "vacationEndDate": "2026-12-24",
   "comment": "Family trip"
   }
3. Create a vacation request
   POST /api/v1/worker/requests
   Input
   4
   {
   "vacationStartDate": "2026-12-22",
   "vacationEndDate": "2026-12-24",
   "comment": "Holiday"
   }
   Output
   {
   "id": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
   "status": "PENDING"
   }
4. Remaining vacation days
   GET /api/v1/worker/remaining-days
   {
   "employeeId": "11111111-1111-1111-1111-111111111111",
   "year": 2026,
   "totalAllowed": 30,
   "takenApproved": 5,
   "remaining": 25
   }
   MANAGER APIs (ROLE_MANAGER)
5. View all vacation requests
   GET /api/v1/admin/requests
   [
   { "id": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", "status": "APPROVED" },
   { "id": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", "status": "PENDING" }
   ]
   5
6. Employee vacation overview
   GET /api/v1/admin/employees/{employeeId}/overview?year=2026
   {
   "employeeId": "11111111-1111-1111-1111-111111111111",
   "name": "Alice",
   "totalAllowed": 30,
   "takenApproved": 5,
   "pendingRequests": [],
   "approvedRequests": []
   }
7. Overlapping vacations
   GET /api/v1/admin/overlaps?year=2026
   [
   {
   "date": "2026-12-22",
   "count": 2,
   "requestsOnDate": [
   { "requestId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" },
   { "requestId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb" }
   ]
   }
   ]
8. Approve or reject vacation
   POST /api/v1/admin/requests/{id}/decision
   Approve
   { "action": "approve" }
   Reject
   6
   { "action": "reject" }
9. Staffing rule error (expected)
   HTTP 400
   {
   "error": "STAFFING_VIOLATION",
   "violations": {
   "2026-12-22": 3
   }
   }
7. Important rules (plain English)
   •
   •
   •
   •
   •
   •
   Each worker gets 30 vacation days per year
   Start and end dates are both counted
   Weekends are counted
   Workers cannot exceed their allowance
   Managers must keep at least 2 employees working
   Tokens control access to APIs
8. Run tests (optional)
   mvn clean test
   Final confirmation
   If you can: - Start the app - Generate tokens - Call the APIs above - See the expected responses
   ✅ The application is working correctly and meets all requirements.
   7