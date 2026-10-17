📦 Warehouse Inventory and Stock Reconciliation System

A RESTful backend application built with Java 17, Spring Boot
4.1.1, Spring Data JPA, Spring Security, JWT,
PostgreSQL, Lombok, and MapStruct.
The application manages products, warehouses, suppliers, stock levels,
inventory movements, and stock reconciliations through REST APIs.

✨ Features
- Product management with unique SKUs.
- Warehouse and supplier management.
- Inventory tracking per product and warehouse.
- Stock receipts, transfers, and dispatches.
- Stock movement history for auditing.
- Low-stock queries based on product reorder levels.
- Physical stock reconciliation and discrepancy reporting.
- Request and response DTOs to separate API contracts from persistence
  entities.
- MapStruct mappers for entity/DTO conversion.
- Bean Validation for request validation.
- Spring Security and JWT authentication, depending on the security
  configuration enabled.
- Transactional stock-changing operations and inventory versioning to
  help protect against concurrent updates.
  
🛠️ Technology Stack
  Technology                    Purpose
  Java 17                       Programming language
  Spring Boot 4.1.1             Application framework
  Spring Web MVC                REST API endpoints
  Spring Data JPA / Hibernate   Persistence and ORM
  PostgreSQL                    Relational database
  Spring Security               Authentication and authorization
  JJWT 0.13.0                   JWT creation and validation
  MapStruct 1.6.3               Compile-time DTO/entity mapping
  Lombok                        Reduces boilerplate in Java classes
  Maven                         Build and dependency management

Package and class names may differ slightly depending on the current
project files.
🧩 Domain Overview
- Product: SKU, name, description, unit price, and reorder level.
- Warehouse: warehouse name and location.
- Supplier: supplier name, email, and phone number.
- Inventory: quantity for a specific product in a specific
  warehouse. A product/warehouse pair is unique.
- StockMovement: records quantity before and after a receipt,
  transfer, or dispatch, with a transaction reference.
- StockReconciliation: stores expected quantity, physical
  quantity, discrepancy, and reference.
  
📋 Prerequisites
- JDK 17
- Maven 3.9+ (or Maven Wrapper, if included)
- PostgreSQL
- An IDE such as IntelliJ IDEA or Eclipse (optional)

🚀 Build and Run
From the project root (the directory containing pom.xml):
mvn clean verify
Start the application:
mvn spring-boot:run
If your application configures a different port, use that port.
Otherwise, Spring Boot normally starts on port 8080.
🔗 REST API Overview
The routes below describe the intended API surface. Confirm exact
mappings and HTTP methods against your controllers.
                                             
The exact JSON fields for receipt, transfer, and dispatch requests
depend on their request DTOs. Use IDs for existing products, warehouses,
and suppliers as defined by those DTOs.

🔍 Reconciliation
  Method                  Endpoint                               Purpose
  POST                  /api/reconciliations                 Record a physical stock
                                                                 count
  GET                   /api/reconciliations/discrepancies   View reconciliation
                                                             discrepancies
                                                             
A reconciliation compares the expected quantity with a physical count.
Verify the service implementation to determine whether it adjusts
inventory automatically or only records the discrepancy.

🔄 DTOs and MapStruct
Controllers should accept request DTOs and return response DTOs rather
than exposing JPA entities directly.
- Request DTOs define and validate client input.
- Response DTOs define the API output.
- MapStruct mappers convert between DTOs and entities at compile
  time.
- Services contain business rules and transaction boundaries.
- Repositories perform database operations.
  
MapStruct and Lombok annotation processing must be configured in Maven.

If generated mapper implementations are missing, run:
mvn clean compile
Then check the compiler output for annotation-processor errors.

🛡️ Inventory Rules and Data Integrity
- A SKU should uniquely identify a product.
- An inventory row represents one product in one warehouse.
- Stock quantities should not become negative.
- Transfers should reduce stock at the source and increase it at the
  destination within one transaction.
- Each stock operation should create auditable movement records.
- A common discrepancy convention is
  physicalQuantity - expectedQuantity; confirm the convention in the
  implementation.
- @Version supports optimistic locking for concurrent inventory
  updates, but the service layer must handle conflicts appropriately.
- Database uniqueness constraints remain important even when Java
  validation is used.
  
🔐 Security Notes
If JWT authentication is configured:
1. Register or provision a user through the supported registration
   workflow.
2. Authenticate through the login endpoint.
3. Include the returned token in protected requests:
Authorization: Bearer <your-jwt-token>
Use the actual login and registration routes implemented by the project.
Configure role-based authorization for administrative changes and stock
operations according to your business requirements.

🧪 Testing
Run the test suite:
mvn test
Recommended scenarios:
- Duplicate SKU is rejected.
- Invalid request fields return a validation error.
- Unknown product, warehouse, or supplier IDs are handled cleanly.
- Dispatching more than the available stock is rejected.
- Transfers update both warehouses correctly.
- Stock movements record correct before/after quantities.
- Reconciliation calculates and reports discrepancies correctly.
- Unauthenticated requests cannot access protected endpoints.
- Concurrent updates do not create negative or inconsistent stock.
  
🩺 Troubleshooting
MapStruct implementation not generated
Run mvn clean compile and check that mapstruct-processor, Lombok,
and lombok-mapstruct-binding are configured in the Maven compiler
annotation processor paths.
Database connection failure
Verify PostgreSQL is running, the database exists, and DB_URL,
DB_USERNAME, and DB_PASSWORD match your local setup.
Validation annotations do not run
Ensure spring-boot-starter-validation is present and controller
request bodies use @Valid.
401 Unauthorized
Check that the request includes a valid bearer token and that the JWT
secret is stable between application restarts.
409 Conflict or optimistic locking failure
Another transaction may have modified the same inventory row. Handle
concurrent updates safely and retry only when the operation is safe to
retry.

🚧 Future Improvements
- Add pagination, sorting, and filtering.
- Add Flyway or Liquibase database migrations.
- Add OpenAPI/Swagger documentation.
- Add integration tests using Testcontainers.
- Add audit metadata and user attribution to stock movements.
- Add idempotency keys for receipt, transfer, and dispatch requests.
- Add Docker Compose for the application and PostgreSQL.
- Add a CI pipeline for build, test, and static analysis.
  
📄 License
Add your preferred license here before distributing the project.
