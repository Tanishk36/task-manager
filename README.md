# TaskFlow – Team Task Manager

A full-stack team task management application built with **Spring Boot** (backend) and **React** (frontend).

---

## 🏗️ Architecture

```
task-manager/
├── backend/          # Spring Boot REST API (Java 17)
└── frontend/         # React SPA
```

---

## 🚀 Quick Start (Local)

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+

### 1. Start Backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs on **http://localhost:8080**

H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:taskmanagerdb`
- User: `sa`, Password: (empty)

### 2. Start Frontend

```bash
cd frontend
npm install
npm start
```

Frontend runs on **http://localhost:3000**

---

## 🔑 Demo Credentials

| Role   | Email                       | Password  |
|--------|-----------------------------|-----------|
| Admin  | admin@taskmanager.com       | admin123  |
| Member | alice@taskmanager.com       | alice123  |
| Member | bob@taskmanager.com         | bob123    |

---

## 📋 Features

### Authentication
- JWT-based signup/login
- Role-based access: **Admin** and **Member**

### Admin Capabilities
- ✅ Create/edit/delete projects
- ✅ Create/edit/delete tasks
- ✅ Assign tasks to team members
- ✅ View all tasks and team members
- ✅ Dashboard with full statistics

### Member Capabilities
- ✅ View assigned tasks only
- ✅ Update task status (Pending → In Progress → Completed)
- ✅ Dashboard with personal stats

### Validation
- Full name: letters and spaces only (2–50 chars)
- Email: valid email format
- Phone: 10–15 digits (optional + prefix)
- Password: minimum 6 characters
- Due date: must be in the future
- Project/task names: minimum 3 characters

---

## 🌐 REST API Endpoints

### Auth
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/login` | Login |

### Tasks (JWT required)
| Method | Path | Role | Description |
|--------|------|------|-------------|
| GET | `/api/tasks` | Admin | All tasks |
| GET | `/api/tasks/my` | Any | My tasks |
| POST | `/api/tasks` | Admin | Create task |
| PUT | `/api/tasks/{id}` | Admin | Update task |
| PATCH | `/api/tasks/{id}/status` | Any | Update status |
| DELETE | `/api/tasks/{id}` | Admin | Delete task |
| GET | `/api/tasks/dashboard` | Any | Dashboard stats |

### Projects (JWT required)
| Method | Path | Role | Description |
|--------|------|------|-------------|
| GET | `/api/projects` | Any | All projects |
| POST | `/api/projects` | Admin | Create project |
| PUT | `/api/projects/{id}` | Admin | Update project |
| DELETE | `/api/projects/{id}` | Admin | Delete project |

### Users (JWT required)
| Method | Path | Role | Description |
|--------|------|------|-------------|
| GET | `/api/users` | Admin | All users |
| GET | `/api/users/me` | Any | Current user |

---

## 🚂 Deploy on Railway

### Step 1: Deploy Backend
1. Create new Railway project
2. Add service → "Deploy from GitHub repo" → select `backend/` folder
3. Railway auto-detects Maven and builds
4. Set env variable: `cors.allowed-origins=https://your-frontend.up.railway.app`

### Step 2: Deploy Frontend
1. Add another service in same Railway project
2. Point to `frontend/` folder
3. Set env variable: `REACT_APP_API_URL=https://your-backend.up.railway.app`
4. Railway builds with `npm run build`

### Step 3: Configure CORS
Update `cors.allowed-origins` in backend to include your Railway frontend URL.

---

## 🗄️ Database

Uses **H2 in-memory** database by default (great for demo/Railway).

### Switch to MySQL (production):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

Add MySQL dependency to `pom.xml`:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 🏛️ Entity Relationships

```
User (1) ──── (N) Task [assignedTo]
User (1) ──── (N) Task [createdBy]  
User (1) ──── (N) Project [createdBy]
Project (1) ── (N) Task
```

---

## 🔒 Security

- JWT tokens with 24h expiration
- BCrypt password hashing
- Method-level security with `@PreAuthorize`
- CORS configured for specified origins
- H2 console disabled in production (set `spring.h2.console.enabled=false`)
