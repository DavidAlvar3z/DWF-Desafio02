📚 Book API – Spring Boot CRUD Project

This project was developed as part of the DWF404 – Web Application Development with Frameworks course at Universidad Don Bosco (UDB).
Its goal is to build a basic REST API for the fictional publisher "Letras Vivas" to manage its book catalog.

---

👨‍💻 Team Members  
- David Alvarez – AM240104 (Repository Owner: @DavidAlvar3z)  
- Ashley Valdez – VG240979 (Collaborator: @achi-vonz)  

---

🚀 Features  
The API includes the following functionalities:  
- List all books  
- Add a new book  
- Search books by title  
- Delete a book by ID  

Additionally, the project includes:  
- Layered architecture (Controller, Service, Repository, Model)  
- Persistence using Spring Data JPA  
- Dependency Injection using @Autowired  
- Configuration through application.properties  
- Basic API documentation with Swagger  

---

🛠️ Technologies Used  
- Java 17  
- Spring Boot 3.x  
- Spring Data JPA  
- H2 Database (in-memory)  
- Swagger (Springdoc OpenAPI)  
- Maven  

---

📥 How to Clone the Repository  
To get a copy of the project locally:  

# Clone the repository  
git clone https://github.com/DavidAlvar3z/DWF-Desafio01.git  

# Navigate into the project folder  
cd DWF-Desafio01  

---

▶️ How to Run the Project  
Make sure you have Java 17 and Maven installed.  

1. Open the project in your IDE (recommended: IntelliJ IDEA or Eclipse).  
2. Run the following command in the terminal:  

mvn spring-boot:run  

The application will start by default at:  
http://localhost:8080  

---

📄 Swagger Documentation  
Swagger UI will be available after running the project.  
Access the documentation and test endpoints from your browser:  

http://localhost:8080/swagger-ui.html  

---

🔧 Endpoints Overview  

Method    Endpoint               Description  
GET       /api/books             List all books  
POST      /api/books             Add a new book  
GET       /api/books/{title}     Search books by title  
DELETE    /api/books/{id}        Delete a book by ID  

---

⚙️ Configuration  
You can edit the file src/main/resources/application.properties to customize the configuration:  

server.port=8080  
spring.datasource.url=jdbc:h2:mem:booksdb  
spring.datasource.driverClassName=org.h2.Driver  
spring.datasource.username=sa  
spring.datasource.password=  
spring.jpa.hibernate.ddl-auto=update  

---

🏆 Extra Improvements  
- Input validation for required fields  
- Centralized error handling using @ControllerAdvice  
- Swagger UI integration for better documentation  

---

📌 Academic Information  
Course: DWF404 – Web Application Development with Frameworks  
Teacher: Miguel Alejandro Meléndez Martínez  
University: Universidad Don Bosco (UDB)  
Year: 2025 – Cycle 2  
Group: 01L  
