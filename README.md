

#Assumptions
1. Payment data batch processing file has only relevant data when importing. No data related to client.

#ER Diagram
db-schema.html
src/main/resources/db-schema.sql

#Local Swagger-ui html
Swagger-UI.html

#swagger-ui
http://localhost:8080/swagger-ui/index.html#/

#OpenAPI APIs doc
http://localhost:8080/api-docs.yaml
api-docs.yaml

#Spring Boot Operations
	#to bootup with security
	$ mvn spring-boot:run -Dspring-boot.run.profiles=local
	
	#to boot up without security
	mvn spring-boot:run -Dspring-boot.run.profiles=non-security
	
	#to test
	$ mvn clean test -Dspring.profiles.active=test
	

	$ mvn clean install

# Maven Dependency Tree
 	mvn dependency:tree

# Maven Dependency Enforcing
	https://maven.apache.org/enforcer/enforcer-rules/index.html

	mvn enforcer:enforce -Drules=requireReleaseDeps
	mvn enforcer:enforce -Drules=banDuplicatePomDependencyVersions


#JCoCo test coverage report
target/site/jacoco/index.html

#Spring Boot Health
http://localhost:8080/actuator/health

#Lombok installation
https://projectlombok.org/setup/eclipse

#Check Style
# Check for violations
	mvn checkstyle:check

# Generate HTML report
	mvn checkstyle:checkstyle

#Pagination
# Default pagination
GET /api/v1/payments

# With pagination parameters
GET /api/v1/payments?page=0&size=50&sort=paymentDate,desc

# Using search endpoint with filters
GET /api/v1/payments/search?page=0&size=20&sortBy=amount&sortDirection=DESC&type=CREDIT&minAmount=100

# Get payments by contract with pagination
GET /api/v1/payments/contracts/C123/payments?paginated=true&page=0&size=10
