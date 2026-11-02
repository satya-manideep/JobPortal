# JobPortal

A job portal web application built with Java Servlets, JSP and MySQL. Recruiters post jobs and review applicants. Job seekers browse openings, apply with a PDF resume and track their applications.

## Features

**Job seekers**
- Register and log in
- Browse and search jobs by keyword, location and job type
- View full job details
- Apply with a PDF resume and an optional cover note (one application per job)
- Track the status of each application: Applied, Shortlisted or Rejected

**Recruiters**
- Register with a company name
- Post jobs and delete their own postings
- See the applicants for each job, with cover notes
- Download applicants' resumes
- Shortlist or reject applicants

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Web | Servlets and JSP (Jakarta EE), custom CSS |
| Database access | JDBC with `PreparedStatement` |
| Database | MySQL 8 |
| Password hashing | BCrypt |
| Server | Apache Tomcat 11 |
| IDE | Eclipse (Dynamic Web Project) |

## Architecture

The project follows the MVC pattern with a separate DAO layer.

| Package | Responsibility |
|---|---|
| `com.jobportal.model` | Plain data classes: `User`, `Job`, `Application` |
| `com.jobportal.dao` | All SQL: `UserDao`, `JobDao`, `ApplicationDao` |
| `com.jobportal.servlet` | Controllers that handle requests and choose the page to show |
| `com.jobportal.filter` | `AuthFilter` protects pages by login status and role |
| `com.jobportal.util` | `DBConnection`, `HtmlUtil`, `DateUtil` |
| `webapp/*.jsp` | Views. Shared header and footer live in `WEB-INF/jspf` |

## Implementation notes

- Passwords are hashed with BCrypt before they are stored.
- Every SQL statement uses `PreparedStatement` with bound parameters.
- Role-based access is enforced by a servlet filter. Recruiter pages and seeker pages are separate.
- Ownership is checked inside the DAO queries, so a recruiter only sees their own jobs and applicants.
- Resumes are stored in a `LONGBLOB` column, accepted only if the file really is a PDF (checked by file signature), and streamed on download.
- Text typed by users is HTML-escaped before it is printed in a page.
- Database settings are read from `db.properties`, which is not committed.

## Getting started

**Requirements:** JDK 21, MySQL 8, Apache Tomcat 11, and Eclipse IDE for Enterprise Java and Web Developers.

1. Clone the repository.
2. Create the database: open `database/schema.sql` in MySQL Workbench and run it.
3. Copy `src/main/java/db.properties.example` to `src/main/java/db.properties` and enter your MySQL user and password.
4. In Eclipse, choose **File → Import → General → Existing Projects into Workspace** and select the project folder.
5. Make sure a Tomcat 11 server is set up under **Window → Preferences → Server → Runtime Environments**, and that the project targets it.
6. Right-click the project and choose **Run As → Run on Server**.
7. Open `http://localhost:8080/Jobportal/` (the address uses your Eclipse project name).

The MySQL connector and BCrypt jars are included in `src/main/webapp/WEB-INF/lib`.

## Project structure

```
Jobportal
├── database/schema.sql
└── src/main
    ├── java
    │   ├── db.properties.example
    │   └── com/jobportal
    │       ├── dao
    │       ├── filter
    │       ├── model
    │       ├── servlet
    │       └── util
    └── webapp
        ├── css
        ├── WEB-INF (jspf, lib)
        └── *.jsp
```

## Possible improvements

- Connection pooling (for example HikariCP)
- Rebuild the same application with Spring Boot and JPA
- Email notification when an application's status changes