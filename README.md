# 📚 Letras Vivas Book API - Enterprise Spring Boot Application

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=spring)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=flat-square&logo=apache-maven)
![JPA](https://img.shields.io/badge/Spring%20Data%20JPA-Enabled-blue?style=flat-square)
![H2](https://img.shields.io/badge/H2%20Database-In%20Memory-yellow?style=flat-square)
![Swagger](https://img.shields.io/badge/OpenAPI-3.0-green?style=flat-square&logo=swagger)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=flat-square)

![GitHub repo size](https://img.shields.io/github/repo-size/DavidAlvar3z/DWF-Desafio01?style=flat-square)
![GitHub last commit](https://img.shields.io/github/last-commit/DavidAlvar3z/DWF-Desafio01?style=flat-square&logo=git)
![GitHub issues](https://img.shields.io/github/issues/DavidAlvar3z/DWF-Desafio01?style=flat-square)
![GitHub pull requests](https://img.shields.io/github/issues-pr/DavidAlvar3z/DWF-Desafio01?style=flat-square)
![GitHub contributors](https://img.shields.io/github/contributors/DavidAlvar3z/DWF-Desafio01?style=flat-square)
![GitHub forks](https://img.shields.io/github/forks/DavidAlvar3z/DWF-Desafio01?style=flat-square&logo=github)
![GitHub stars](https://img.shields.io/github/stars/DavidAlvar3z/DWF-Desafio01?style=flat-square&logo=github)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen?style=flat-square&logo=github-actions)
![Coverage](https://img.shields.io/badge/coverage-85%25-blue?style=flat-square)
![Status](https://img.shields.io/badge/status-active-success?style=flat-square)

This enterprise-grade REST API was developed as part of the **DWF404 – Web Application Development with Frameworks** course at Universidad Don Bosco (UDB). It provides a comprehensive solution for the fictional publisher "Letras Vivas" to manage their complete business ecosystem including books, users, and subscriptions.

---

## 👨‍💻 Team Members  
- **David Alvarez** – AM240104 (Repository Owner: [@DavidAlvar3z](https://github.com/DavidAlvar3z))  
- **Ashley Valdez** – VG240979 (Collaborator: [@achi-vonz](https://github.com/achi-vonz))  

---

## 🚀 Features Overview

### 📖 Book Management
- **Complete CRUD operations** for book catalog
- **Advanced search capabilities** (title, author, genre, ISBN, publication year)
- **Bulk operations** for multiple book management
- **Availability tracking** (soft delete functionality)
- **Classification system** (classics, recent books, genre-based filtering)
- **Analytics and statistics** (popular genres, prolific authors)
- **ISBN validation** and duplicate detection
- **Similarity recommendations** based on content

### 👥 User Management
- **User registration and profile management**
- **Advanced search and filtering** (name, email, age range)
- **User statistics and analytics**
- **Soft delete with activity status**
- **Email validation and duplicate prevention**
- **Age-based user categorization**

### 📅 Subscription Management
- **Comprehensive subscription lifecycle management**
- **Multiple subscription statuses** (Active, Inactive, Suspended, Expired, Cancelled)
- **Revenue tracking and analytics**
- **Expiration notifications and automatic status updates**
- **Plan popularity analytics**
- **Advanced search with multiple criteria**
- **Auto-renewal capabilities**
- **Price range filtering**

### 🏗️ Architecture & Quality Features
- **Layered architecture** (Controller → Service → Repository → Entity)
- **Comprehensive exception handling** with custom exceptions
- **Input validation** with detailed error messages
- **Complete API documentation** with Swagger/OpenAPI 3.0
- **Extensive test coverage** (Unit, Integration, Repository tests)
- **Transaction management** with Spring @Transactional
- **Pagination and sorting** for all list endpoints
- **CORS configuration** for frontend integration

---

## 🛠️ Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.x |
| **Data Access** | Spring Data JPA | Latest |
| **Database** | H2 (Development) | In-Memory |
| **Validation** | Bean Validation (JSR-303) | Latest |
| **Documentation** | Springdoc OpenAPI | 3.0 |
| **Build Tool** | Maven | 3.8+ |
| **Testing** | JUnit 5, Mockito | Latest |
| **Architecture** | RESTful API, MVC Pattern | - |

---

## 📥 Installation & Setup

### Prerequisites
- **Java 17** or higher
- **Maven 3.8+**
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code)

### Clone Repository
```bash
# Clone the repository
git clone https://github.com/DavidAlvar3z/DWF-Desafio02.git

# Navigate to project directory
cd DWF-Desafio02
```

### Build & Run
```bash
# Clean and install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start at: **http://localhost:8080**

---

## 📄 API Documentation

### Swagger UI
Access the interactive API documentation at:
**http://localhost:8080/swagger-ui.html**

### OpenAPI Specification
Raw OpenAPI spec available at:
**http://localhost:8080/v3/api-docs**

---

## 🔧 API Endpoints Overview

### 📚 Books API (`/api/v1/books`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| **GET** | `/api/v1/books` | Get all books (paginated) |
| **GET** | `/api/v1/books/{id}` | Get book by ID |
| **POST** | `/api/v1/books` | Create new book |
| **PUT** | `/api/v1/books/{id}` | Update book |
| **DELETE** | `/api/v1/books/{id}` | Delete book |
| **GET** | `/api/v1/books/search` | Search books (title/author) |
| **GET** | `/api/v1/books/search/advanced` | Advanced multi-criteria search |
| **GET** | `/api/v1/books/genre/{genre}` | Get books by genre |
| **GET** | `/api/v1/books/available` | Get available books |
| **GET** | `/api/v1/books/classics` | Get classic books (pre-1950) |
| **GET** | `/api/v1/books/recent` | Get recent books (2020+) |
| **GET** | `/api/v1/books/isbn/{isbn}` | Get book by ISBN |
| **POST** | `/api/v1/books/bulk` | Create multiple books |
| **PATCH** | `/api/v1/books/{id}/unavailable` | Mark book unavailable |
| **GET** | `/api/v1/books/analytics/genres/popular` | Most popular genres |

### 👥 Users API (`/api/v1/users`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| **GET** | `/api/v1/users` | Get all users (paginated) |
| **GET** | `/api/v1/users/{id}` | Get user by ID |
| **POST** | `/api/v1/users` | Create new user |
| **PUT** | `/api/v1/users/{id}` | Update user |
| **DELETE** | `/api/v1/users/{id}` | Soft delete user |
| **DELETE** | `/api/v1/users/{id}/permanent` | Permanently delete user |
| **GET** | `/api/v1/users/search` | Search users by name |
| **GET** | `/api/v1/users/active` | Get active users |
| **GET** | `/api/v1/users/age-range` | Get users by age range |
| **GET** | `/api/v1/users/with-subscriptions` | Users with active subscriptions |
| **GET** | `/api/v1/users/advanced-search` | Advanced user search |
| **GET** | `/api/v1/users/email/{email}` | Get user by email |
| **GET** | `/api/v1/users/statistics` | User statistics |

### 📅 Subscriptions API (`/api/v1/subscriptions`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| **GET** | `/api/v1/subscriptions` | Get all subscriptions (paginated) |
| **GET** | `/api/v1/subscriptions/{id}` | Get subscription by ID |
| **POST** | `/api/v1/subscriptions` | Create new subscription |
| **PUT** | `/api/v1/subscriptions/{id}` | Update subscription |
| **DELETE** | `/api/v1/subscriptions/{id}` | Delete subscription |
| **PATCH** | `/api/v1/subscriptions/{id}/cancel` | Cancel subscription |
| **GET** | `/api/v1/subscriptions/user/{userId}` | Get user subscriptions |
| **GET** | `/api/v1/subscriptions/status/{status}` | Get subscriptions by status |
| **GET** | `/api/v1/subscriptions/expiring` | Get expiring subscriptions |
| **PATCH** | `/api/v1/subscriptions/update-expired` | Update expired subscriptions |
| **GET** | `/api/v1/subscriptions/revenue` | Calculate revenue by date range |
| **GET** | `/api/v1/subscriptions/popular-plans` | Most popular subscription plans |
| **GET** | `/api/v1/subscriptions/advanced-search` | Advanced subscription search |

---

## ⚙️ Configuration

### Application Properties
```properties
# Server Configuration
server.port=8080

# Database Configuration (H2 In-Memory)
spring.datasource.url=jdbc:h2:mem:letrasvivas_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console (Development)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Validation Configuration
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false

# OpenAPI Documentation
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
```

---

## 🧪 Testing

### Run All Tests
```bash
# Run all tests
mvn test

# Run with coverage report
mvn clean test jacoco:report
```

### Test Categories
- **Unit Tests**: Service layer business logic
- **Integration Tests**: Controller endpoints
- **Repository Tests**: Data access layer
- **Validation Tests**: Input validation scenarios

### Test Coverage
- **Controllers**: Complete endpoint testing
- **Services**: Business logic validation
- **Repositories**: Custom query testing
- **Exception Handling**: Error scenario coverage

---

## 📊 Database Schema

### Entities Overview
- **Book**: Catalog management with ISBN, genre, availability
- **User**: Customer information with activity status
- **Subscription**: Service subscriptions with lifecycle management

### Relationships
- **User → Subscriptions**: One-to-Many relationship
- **Advanced Queries**: Custom repository methods for complex searches
- **Soft Delete**: Logical deletion maintaining data integrity

---

## 🔒 Error Handling

### Custom Exception Types
- **ResourceNotFoundException**: 404 errors for missing entities
- **DuplicateResourceException**: 409 conflicts for duplicate data
- **BusinessValidationException**: 400 business rule violations
- **DatabaseOperationException**: 500 database operation failures

### Standardized Error Response
```json
{
  "timestamp": "2025-01-20T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Book not found with id: 123",
  "path": "/api/v1/books/123",
  "validationErrors": {},
  "details": {
    "resourceName": "Book",
    "fieldName": "id",
    "fieldValue": 123
  }
}
```

---

## 🏆 Additional Features

### Validation Features
- **Bean Validation (JSR-303)** annotations
- **Custom validation** for business rules
- **Email format validation**
- **Phone number pattern validation**
- **ISBN format validation**
- **Date range validation**

### Performance Features
- **Pagination** for large datasets
- **Lazy loading** for entity relationships
- **Query optimization** with custom repository methods
- **Transaction management** for data consistency

### Security Features
- **Input validation** and sanitization
- **CORS configuration** for frontend integration
- **SQL injection prevention** through JPA
- **Data integrity constraints**

---

## 📌 Academic Information

| Field | Details |
|-------|---------|
| **Course** | DWF404 – Web Application Development with Frameworks |
| **Institution** | Universidad Don Bosco (UDB) |
| **Professor** | Miguel Alejandro Meléndez Martínez |
| **Academic Period** | 2025 – Cycle 2 |
| **Group** | 01L |
| **Project Type** | Enterprise REST API Development |

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

This is an academic project, but contributions and suggestions are welcome:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📞 Support

For questions or support related to this project:

- **Repository Issues**: [GitHub Issues](https://github.com/DavidAlvar3z/DWF-Desafio02/issues)
- **Academic Support**: Contact course instructor
- **Technical Documentation**: Swagger UI at `/swagger-ui.html`

---
