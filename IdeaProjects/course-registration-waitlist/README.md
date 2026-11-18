
# Course Registration and Waitlist Management

## 1. Overview

This project implements a course registration and waitlist management system using Java 21 and Spring Boot.

The application allows learners to:
- Register for a course when seats are available.
- Join a waitlist when the course is full.
- Drop an existing registration.
- Get automatically promoted from the waitlist when a seat becomes available.

The waitlist follows the First In, First Out (FIFO) policy.

### Data Persistence

The application currently uses in-memory storage
with LinkedHashMap to maintain enrolled and
waitlisted learners.

Registration data is maintained while the
application is running. Data is cleared when
the application stops or restarts.

Persistent database storage is considered
outside the scope of this prototype.

## 2. Technology Stack

- Java 21
- Spring Boot
- Maven
- JUnit 5
- HTML
- CSS
- JavaScript
- In-memory data storage

## 3. Application Features

### Learner Registration
- Learners can register when course capacity is available.
- Eligible learners are added to the waitlist when the course is full.
- Duplicate registrations are prevented.
- Learners who have not satisfied the prerequisite are rejected.

### Course Drop
- Enrolled learners can drop the course.
- Dropping an enrolled learner frees a seat.
- The next eligible learner is automatically promoted.

### Waitlist Management
- Waitlist promotion follows FIFO order.
- Ineligible waitlisted learners are skipped during promotion.
- Their original waitlist position is retained.

### Web Dashboard
- View course capacity and available seats.
- Register learners through a web form.
- View enrolled learners.
- View waitlisted learners and their positions.
- Drop enrolled learners through the dashboard.
- View promotion results.

## 4. Business Rules and Assumptions

1. The application manages one course offering.
2. Course capacity is configurable.
3. The default course capacity is 2.
4. The waitlist has no configured maximum size.
5. A learner must have a valid learner ID.
6. A learner must satisfy the course prerequisite to register.
7. A learner can be enrolled or waitlisted only once.
8. Waitlist promotion follows FIFO order.
9. Ineligible waitlisted learners remain on the waitlist but are skipped during promotion.
10. Registration data is stored in memory and is not retained after application restart.

For this prototype, prerequisite eligibility is represented by a boolean value.

## 5. Project Structure

```text
src/
├── main/
│   ├── java/com/schwab/registration/
│   │   ├── CourseRegistrationApplication.java
│   │   ├── CourseRegistrationDemo.java
│   │   ├── controller/
│   │   │   └── RegistrationController.java
│   │   ├── model/
│   │   │   ├── Learner.java
│   │   │   ├── RegistrationResult.java
│   │   │   └── RegistrationStatus.java
│   │   └── service/
│   │       └── CourseRegistrationService.java
│   │
│   └── resources/static/
│       ├── index.html
│       ├── style.css
│       └── app.js
│
└── test/
    └── java/com/schwab/registration/service/
        └── CourseRegistrationServiceTest.java
```

## 6. Running the Application

### Prerequisites

- JDK 21
- IntelliJ IDEA or another Java IDE
- Maven

### Start the Web Application

Run `CourseRegistrationApplication.java` from IntelliJ.

Alternatively, if Maven is installed:

```bash
mvn spring-boot:run
```

Open the dashboard in a browser:

```text
http://localhost:8080
```

### Run the Console Demonstration

Run `CourseRegistrationDemo.java` from IntelliJ.

### Run Unit Tests

Run:

```bash
mvn test
```

## 7. REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/registrations` | Retrieve enrollment and waitlist information |
| POST | `/api/registrations` | Register a learner |
| DELETE | `/api/registrations/{learnerId}` | Drop an enrolled learner |

### Registration Request Example

```json
{
  "learnerId": "L101",
  "prerequisiteSatisfied": true
}
```

## 8. Example Registration Flow

For a course with capacity 2:

| Learner | Registration Result |
|---|---|
| L101 | Enrolled |
| L102 | Enrolled |
| L103 | Waitlisted |
| L104 | Waitlisted |

When L101 drops the course:
- L103 is automatically promoted.
- L104 remains on the waitlist.

## 9. Testing

Unit tests cover:
- Successful enrollment.
- Waitlist registration when capacity is full.
- Duplicate registration prevention.
- Prerequisite validation.
- FIFO waitlist promotion.
- Skipping ineligible waitlisted learners.
- Drop operation behavior.

## 10. Limitations and Future Improvements

- Uses in-memory storage instead of a persistent database.
- Supports one course offering.
- Does not include authentication or authorization.
- Does not integrate with an external prerequisite verification system.
- Does not persist registrations after application restart.

Possible future improvements include database persistence, multiple course offerings, authentication and role-based access control.

## 11. AI-Assisted Development

AI tools were used as engineering assistance during implementation, code review and documentation.

The final implementation and design decisions should be reviewed and understood by the developer. Refer to the AI usage record for the actual prompts, suggestions and review decisions.
