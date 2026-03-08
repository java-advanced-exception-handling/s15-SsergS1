# Advanced Java Course
## Web Application 'ToDo List'

## 📋 Project Overview
This is a Spring MVC web application for managing ToDo lists with tasks. The project implements a complete CRUD functionality for Users, ToDo lists, Tasks, and States.

## 🎯 Sprint 15: Exception Handling
1. Create package `exception` and implement `GlobalExceptionHandler` - exception handling for custom exception (e.g., in `UserControllerAdvice`).
2. Create "Error" page. Page should contain information about exception (message exception).
3. Create 404 page and 500 page.
4. Implement exception handling with redirection to "Error" page (Global handling is partially implemented for `Task` and `State`).
5. **Student Task**: Implement exception handling specifically for `UserController` and `ToDoController` to handle `EntityNotFoundException` and redirect to the 404 error page.
    - Students can choose to implement this locally using `@ExceptionHandler` within each controller OR expand the `UserControllerAdvice` to handle these controllers (or all controllers).
    - Ensure that when a user, todo, or collaborator is not found (e.g., via `userService.readById(id)` or `todoService.readById(id)`), the application displays the `error/404` page with a relevant message.
    - **Optional**: Students can also think about handling 401 (Unauthorized) and 403 (Forbidden) errors, which will be needed in the next sprint for security implementation.
6. **Logging**: Implement logging for all major operations and error cases.
    - Use SLF4J `log.info()`, `log.warn()`, and `log.error()` to record application flow and exceptions.
    - **Student Task**: Ensure that your new exception handling logic for `UserController` and `ToDoController` includes appropriate logging of the caught exceptions.
7. On service layer methods:
    - `create` and `update` should throw `NullEntityReferenceException` when user tries to create or update an empty object.
    - `find...` and `delete` should throw `EntityNotFoundException` when user tries to read or delete a non-existent object.
---

## 🚀 How to Run
1. Ensure PostgreSQL is running with database `todolist` (or use H2 configuration for quick start).
2. Run the Spring Boot application.
3. Navigate to `http://localhost:8083`.
4. Login with credentials provided in the database (e.g., from `data.sql`).

## 📚 Technologies Used
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- PostgreSQL / H2
- Lombok
- Bootstrap 5
- Jakarta Validation
- Java 21


