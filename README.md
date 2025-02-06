# Cafe Management System (Backend)

## 📌 Project Overview
This repository contains the backend implementation of the **Cafe Management System** built using **Java** and **Spring Boot**. It provides a robust API for managing cafe-related operations, including authentication, user management, and data handling. The project follows a well-structured package hierarchy and integrates **Spring Security** with JWT authentication.

## 📌 Features
✔️ User Authentication & Authorization (JWT-based)  
✔️ Secure API Endpoints with Role-Based Access Control  
✔️ Database Integration with MySQL  
✔️ Modular and Clean Code Structure  
✔️ RESTful API Design  
✔️ Sends mail to registered users using **JavaMailSender**  
✔️ Only **Admin** can change the user status (active/inactive)  
✔️ All **Admins** receive an email notification when an admin updates a user’s status  
✔️ Generates a **PDF bill** for purchased products, including name, category, quantity, price, subtotal & total price  
✔️ PDF bill contains user details (name, contact, email, payment method) and a **Thank You** note  
✔️ Forgot Password functionality – sends reset password mail to the registered user  

## 🛠️ Tech Stack
- **Java**
- **Spring Boot 3.x**
- **Spring Security 6**
- **Maven**
- **MySQL**
- **Git & GitHub**

### 🏗️ Spring Boot Dependencies
- **Spring Boot Starter Web** – For building RESTful web applications
- **Spring Boot Starter Security** – For implementing authentication & authorization
- **Spring Boot Starter Data JPA** – For database interactions using MySQL
- **Spring Boot Starter Mail** – For sending emails using JavaMailSender
- **Spring Boot Starter Thymeleaf** – For generating PDF files
- **Spring Boot Starter Validation** – For handling validation of request data

## 🔐 Authentication & Security
This project implements **JWT (JSON Web Token) authentication** using **Spring Security**. 
- The `JWT` package contains utility classes for generating, validating, and parsing JWT tokens.
- It ensures secure access to API endpoints by restricting unauthorized requests.

### 🔑 JWT Package Overview
- **JwtUtil.java** – Utility class for JWT operations:
  - Generates, extracts, and validates JWT tokens to ensure secure authentication.
  - Retrieves claims such as username and expiration details from tokens.
  - Checks for token expiration and ensures validity.

- **JwtFilter.java** – Handles JWT-based authentication:
  - Intercepts HTTP requests to extract and validate JWT tokens.
  - If the token is valid, sets authentication in the security context.
  - Provides methods to check user roles and retrieve the current user.

- **CustomerUserDetailsService.java** – Implements user authentication:
  - Loads user details from the database during authentication.
  - Retrieves and provides user-specific data for security processing.

- **SecurityConfig.java** – Configures security settings:
  - Defines JWT authentication filter and request authorization rules.
  - Manages password encoding, authentication manager, and security filter chain.


## 📖 API ENDPOINT GUIDE

### APIs without security authentication:
- **`/login`** – Authenticate and login user
- **`/signup`** – Register new users
- **`/forgot-password`** – Sends password reset email using JavaMailSender

### APIs with security authentication & validation:

#### 📊 DASHBOARD:
- Get **dashboard details** (categories, products, bills)

#### 👤 USER:
- **`/update`** – Update user details
- **`/change-password`** – Change user password

#### 🛠️ ADMIN:
- Get all registered users with their status (active/inactive)

#### 📂 CATEGORY:
- **`POST`** Add new category
- **`GET`** Get all categories
- **`PUT`** Update category

#### 🏷️ PRODUCT:
- **`POST`** Add new product
- **`GET`** Get all products
- **`GET`** Get products by category (only available products)
- **`GET`** Get product by ID
- **`PUT`** Update product
- **`PUT`** Update product status
- **`DELETE`** Delete product by ID

#### 🧾 BILL:
- **`POST`** Generate bill
- **`GET`** Get bills by username
- **`POST`** Get PDF bill by UUID
- **`DELETE`** Delete bill by ID (different from UUID)

---

## 📂 Package Structure
```yaml
📂 inn.cafe
├── 📦 constants         # Contains application-wide constants
├── 📂 dao               # Data Access Objects (Repositories for database interactions)
├── 🔐 JWT               # Handles JWT authentication & token management
├── 📦 POJO              # Plain Old Java Objects (Models representing database entities)
├── 🌍 rest              # Controller layer (Handles API requests)
├── ⚙️ restImpl          # Implementation of REST APIs
├── 🔧 service           # Service interfaces (Business logic layer)
├── 🏗️ serviceImpl       # Implementation of service interfaces
├── 🛠️ utils             # Utility classes (Helper methods, validations, etc.)
└── 📦 wrapper           # Custom response wrappers for API responses

📂 resources
└── 📄 application.properties  # Spring Boot configuration file (Database, JWT settings, etc.)
```

## 🚀 How to Run the Project
### 1️⃣ Clone the Repository
```sh
git clone https://github.com/tushar-swarnkar/cafe-management-system.git
cd cafe-management-system
```

### 2️⃣ Configure Database
- Update `application.properties` with your **MySQL database credentials**.

### 3️⃣ Build and Run the Project
```sh
mvn clean install
mvn spring-boot:run
```

### 4️⃣ API Testing
Use **Postman** or any API testing tool to test the endpoints.
---

