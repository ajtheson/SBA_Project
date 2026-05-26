# Quiz Online System

Hệ thống thi trắc nghiệm trực tuyến - cho phép giáo viên tạo bài quiz, đề thi và học sinh tham gia làm bài.

## Tech Stack

| Layer     | Technology                          |
|-----------|-------------------------------------|
| Front-End | ReactJS (Vite)                      |
| Back-End  | Spring Boot 3.2 (RESTful API)       |
| Database  | SQL Server + JPA/Hibernate          |
| Security  | Spring Security (Authentication + Authorization) |

## Cấu trúc thư mục

```
SBA_Project/
├── docs/                  # Tài liệu thiết kế (Use Case, ERD, DDL)
│   ├── database/          # DDL.sql, InsertData.sql
│   └── usecase/           # Use Case Diagram
├── backend/               # Spring Boot project (Maven)
│   ├── pom.xml
│   └── src/main/java/com/quizonline/
│       ├── config/        # Cấu hình (Security, CORS, ...)
│       ├── controller/    # REST Controllers
│       ├── dto/           # Data Transfer Objects
│       ├── entity/        # JPA Entities
│       ├── repository/    # JPA Repositories
│       └── service/       # Business Logic
├── frontend/              # ReactJS project (Vite)
│   ├── package.json
│   └── src/
│       ├── api/           # Axios instance
│       ├── components/    # Reusable components
│       ├── context/       # React Context (auth, ...)
│       ├── pages/         # Page components
│       └── services/      # API service functions
└── README.md
```

## Yêu cầu

- **Java 21+**
- **Node.js 20+**
- **SQL Server** (đang chạy, database `QuizOnlineSystem` đã tạo)
- **Maven Wrapper** (có sẵn trong `backend/`)

## Hướng dẫn chạy

### 1. Database

Chạy file `docs/database/DDL.sql` trong SQL Server Management Studio để tạo database và các bảng.  
_(Tuỳ chọn)_ Chạy `docs/database/InsertData.sql` để thêm dữ liệu mẫu.

### 2. Backend

```bash
cd backend

# Cập nhật thông tin kết nối SQL Server trong:
# src/main/resources/application.properties
# (username, password, port)

# Chạy ứng dụng
# Windows
.\mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend sẽ chạy tại: `http://localhost:5173`
