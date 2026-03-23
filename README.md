# 🏦 Bank Management System

A full-stack banking application built with **Spring Boot** (backend) and **HTML/CSS/JS** (frontend).

---

## 🚀 Tech Stack

| Layer    | Technology                           |
|----------|--------------------------------------|
| Backend  | Spring Boot 3.5.9, Java 21           |
| Security | Spring Security, JWT (JJWT 0.11.5)   |
| Database | MySQL 8, Spring Data JPA, Hibernate  |
| Frontend | HTML5, CSS3, Bootstrap 5, Vanilla JS |
| Build    | Maven                                |

---

## ✨ Features

- User Signup with OTP verification
- JWT Authentication — stateless, secure
- Role-based access — USER and ADMIN roles
- Deposit, Withdraw, Transfer money between accounts
- Transaction History with search and filter
- Admin Panel — view all users, search by username
- BigDecimal for all financial calculations
- No browser alert/confirm popups — toast notifications

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/example/bank_management_system/
│   │   ├── BankManagementSystemApplication.java
│   │   ├── config/
│   │   │   └── JwtProperties.java
│   │   ├── controller/
│   │   │   ├── AccountController.java
│   │   │   ├── AdminController.java
│   │   │   ├── AuthController.java
│   │   │   └── TransactionController.java
│   │   ├── dto/
│   │   │   ├── AccountResponseDto.java
│   │   │   ├── AdminAccountResponseDto.java
│   │   │   ├── ApiResponse.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── OtpRequest.java
│   │   │   ├── ResendOtpRequest.java
│   │   │   ├── SignupRequest.java
│   │   │   ├── TransactionDto.java
│   │   │   ├── TransactionRequest.java
│   │   │   └── TransferRequest.java
│   │   ├── entity/
│   │   │   ├── Account.java
│   │   │   ├── OtpVerification.java
│   │   │   ├── Transaction.java
│   │   │   ├── User.java
│   │   │   └── enums/
│   │   │       ├── AccountType.java
│   │   │       ├── Role.java
│   │   │       ├── TransactionStatus.java
│   │   │       └── TransactionType.java
│   │   ├── exception/
│   │   │   ├── CustomException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── repository/
│   │   │   ├── AccountRepository.java
│   │   │   ├── OtpRepository.java
│   │   │   ├── TransactionRepository.java
│   │   │   └── UserRepository.java
│   │   ├── security/
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtUtil.java
│   │   │   └── SecurityConfig.java
│   │   └── service/
│   │       ├── AccountService.java
│   │       ├── AdminService.java
│   │       ├── AuthService.java
│   │       └── TransactionService.java
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   │   ├── bootstrap.min.css
│       │   │   ├── dashboard.css
│       │   │   ├── login.css
│       │   │   ├── otp.css
│       │   │   └── signup.css
│       │   ├── js/
│       │   │   ├── admin-dashboard.js
│       │   │   ├── dashboard.js
│       │   │   ├── login.js
│       │   │   ├── otp.js
│       │   │   └── signup.js
│       │   ├── image/
│       │   ├── admin-dashboard.html
│       │   ├── dashboard.html
│       │   ├── login.html
│       │   ├── otp.html
│       │   └── signup.html
│       └── application.properties.example
└── test/
    └── java/com/example/bank_management_system/
        └── BankManagementSystemApplicationTests.java
```

---

## ⚙️ Setup & Run

### 1. Prerequisites
- Java 21+
- MySQL 8
- Maven

### 2. Clone the repo
```bash
git clone https://github.com/Chiragcodd/bank-management-system.git
cd bank-management-system
```

### 3. Database setup
```sql
CREATE DATABASE bank_db;
```

### 4. Create application.properties
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Fill in your values:
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
jwt.secret=YOUR_SECRET_KEY_MIN_32_CHARACTERS
```

### 5. Set Environment Variables

**Windows:**
```cmd
set DB_PASSWORD=your_mysql_password
set SECERT_KEYBANK=your_secret_key_min_32_chars
```

**Mac/Linux:**
```bash
export DB_PASSWORD=your_mysql_password
export SECERT_KEYBANK=your_secret_key_min_32_chars
```

### 6. Run
```cmd
mvn spring-boot:run
```

App will start at: **http://localhost:8080**

---

## 🔐 API Endpoints

### Auth — Public
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/verify-otp` | Verify OTP |
| POST | `/api/auth/resend-otp` | Resend OTP |
| POST | `/api/auth/login` | Login |

### Account — JWT Required
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/account/me` | Get my account |
| POST | `/api/account/deposit` | Deposit money |
| POST | `/api/account/withdraw` | Withdraw money |
| POST | `/api/account/transfer` | Transfer money |
| GET | `/api/account/transactions` | Transaction history |

### Admin — ADMIN Role Required
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/all-accounts` | Get all accounts |
| GET | `/api/admin/search?username=` | Search by username |

---

## 📝 Notes

- OTP is printed to the **server console/terminal** (development mode)
- `application.properties` is excluded from git — secrets are safe
- All financial amounts use `BigDecimal` — no floating point errors