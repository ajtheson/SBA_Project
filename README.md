# Quiz Practicing System

Hệ thống luyện tập trắc nghiệm trực tuyến.

## Mô tả

Ứng dụng web full-stack với các chức năng:

- Customer: Đăng ký khóa học, làm quiz
- Expert: Quản lý bài giảng, câu hỏi
- Sale: Quản lý đăng ký
- Admin: Quản lý hệ thống

## Công nghệ

- Backend: Spring Boot 3.2.2, Java 17, SQL Server, JPA, Spring Security + JWT
- Frontend: React 18, Vite, React Router, Axios

## Yêu cầu

- Java 17+
- Node.js 18+
- SQL Server 2019+
- Maven 3.8+

## Hướng dẫn chạy

### 1. Database

Chạy 2 file SQL theo thứ tự:

1. `docs/database/ddl_official.sql` - Tạo database
2. `docs/database/dml_official.sql` - Thêm dữ liệu mẫu

### 2. Backend

```bash
cd backend
```

Sửa file `src/main/resources/application.properties`:

```properties
spring.datasource.password=YOUR_PASSWORD
```

Chạy:

```bash
mvn spring-boot:run
```

Truy cập: http://localhost:8080

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Truy cập: http://localhost:5173

## Tài khoản test

| Role     | Email               | Password |
| -------- | ------------------- | -------- |
| Admin    | admin@gmail.com     | Pass123@ |
| Expert   | expert1@example.com | Pass123@ |
| Customer | alice@example.com   | Pass123@ |
| Sale     | sale1@example.com   | Pass123@ |
