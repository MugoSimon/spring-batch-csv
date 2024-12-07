# Spring Batch CSV Processor

This project demonstrates a Spring Batch implementation for processing CSV files, parsing customer data, and saving it to a database. The batch job reads a CSV file, validates its content, and writes the data to a relational database.

---

## Features
- **CSV File Processing**: Reads customer data from a CSV file.
- **Validation**: Ensures data integrity using field-level constraints.
- **Database Integration**: Persists validated customer data into the database.
- **Parallel Processing**: Utilizes a thread pool for efficient processing.
- **Error Handling**: Provides meaningful logs for debugging and troubleshooting.

---

## Prerequisites
1. **Java**: Java 17 or higher.
2. **Spring Framework**: Spring Boot 3.x with Spring Batch.
3. **Database**: A relational database such as MySQL, PostgreSQL, or H2 (configurable).
4. **Maven**: For dependency management and building the project.

---

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/thimionii/spring_batch_csv/
│   │       ├── config/      # Batch and application configuration
│   │       ├── entity/      # JPA entities (e.g., Customer)
│   │       ├── processor/   # Custom processors
│   │       ├── repo/        # Repository interfaces for database access
│   │       └── service/     # Business logic services
│   └── resources/
│       ├── application.yml  # Configuration file
│       └── random_users.csv # Sample CSV input
```

---

## CSV File Format

The input CSV file should have the following format:

| id | firstName | lastName | email                     | gender      | contactNo      | country | dob        |
|----|-----------|----------|---------------------------|-------------|----------------|---------|------------|
| 1  | John      | Doe      | john.doe@example.com      | male        | 970-123-456    | USA     | 1980-01-01 |
| 2  | Jane      | Smith    | jane.smith@example.com    | female      | 970-234-567    | Canada  | 1990-02-02 |

- **Header Row**: The first row of the file contains column headers.
- **Data Rows**: Each subsequent row represents a customer.

### Field Requirements:
- `firstName`, `lastName`: Required, max 50 characters.
- `email`: Valid email format.
- `gender`: One of `male`, `female`, or `genderqueer`.
- `contactNo`: Must follow the pattern `970-XXX-XXX`.
- `dob`: Must be a past date.

---

## Configuration

### Application Properties
Configure file paths and database settings in `application.yml`:

```yaml
csv:
  file:
    path: "C:\\Users\\Simon.Wangechi\\IdeaProjects\\spring-batch-csv\\src\\main\\resources\\random_users.csv"

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: update
```

---

## How to Run

### 1. Build the Project
Use Maven to build the project:
```bash
mvn clean install
```

### 2. Run the Application
Start the application using:
```bash
mvn spring-boot:run
```

### 3. Monitor the Logs
Check the logs to verify if the job ran successfully:
```log
INFO  - Job [csv-job] started.
INFO  - Step: [csv-step] executed successfully.
```

---

## Error Handling

### Common Issues
- **File Not Found**: Ensure the CSV file path in the `csv.file.path` property is correct.
- **Validation Errors**: Review constraints in the `Customer` entity (e.g., invalid email, missing fields).
- **Database Connection Issues**: Verify the database URL and credentials.

### Logs
Detailed logs are provided for debugging. Example:
```log
ERROR - Parsing error at line: 5 in resource=[file [random_users.csv]]
```

---

## Testing

### File Existence
The application checks if the CSV file exists before processing. If not, an error is logged, and the process terminates.

---

## License
This project is open-source and available under the MIT License.

--- 
Feel free to contribute or raise issues for improvements!
```
