# 💼 Expense Management System

A full-stack **Expense Management Application** built with **Spring Boot**, **React**, **Tailwind CSS**, and **PostgreSQL**.  
It helps manage expenses, users, and approvals in a clean and efficient way.

---

## 🛠️ Tech Stack

| Layer | Technology |
|--------|-------------|
| **Frontend** | React + Tailwind CSS |
| **Backend** | Spring Boot (Java) |
| **Database** | PostgreSQL |
| **Build Tools** | Maven (backend), npm (frontend) |

---

## ⚙️ Features

- Add, view, and manage expenses  
- Role-based access (Admin, Manager, Employee)  
- Approval workflows for submitted expenses  
- Responsive UI with Tailwind CSS  
- RESTful API built with Spring Boot  
- PostgreSQL as the main data store  

---

## 🚀 Setup Instructions

### 🧩 Backend (Spring Boot)
1. Navigate to the backend folder:
   ```bash
   cd backend
   ```
2. Configure PostgreSQL in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/expense_tracker
   spring.datasource.username=abhi
   spring.datasource.password=abhi
   spring.jpa.hibernate.ddl-auto=update
   ```
3. Run the backend:
   ```bash
   mvn spring-boot:run
   ```
4. The API runs at:  
   👉 `http://localhost:8080`

---

### 💻 Frontend (React + Tailwind)
1. Navigate to the frontend folder:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. The React app runs at:  
   👉 `http://localhost:5173` (or your configured port)

---

## 🧾 Database

Make sure PostgreSQL is running and a database is created:
```sql
CREATE DATABASE expense_tracker;
CREATE USER abhi WITH PASSWORD 'abhi';
GRANT ALL PRIVILEGES ON DATABASE expense_tracker TO abhi;
```

---

## 🧰 Tools Used

- **Spring Boot DevTools** – live reload for backend  
- **React Router** – navigation  
- **Axios / Fetch API** – backend communication  
- **Tailwind CSS** – clean and responsive styling  

---

## 📘 API Docs

Swagger UI (for backend API docs):  
👉 `http://localhost:8080/swagger-ui/index.html`

---

## 👨‍💼 Author

**Abhishek**  
💻 *Spring Boot | React | Tailwind | PostgreSQL Developer*  

---

> ⚡ *Full-stack expense tracker — simple, modular, and modern.*
