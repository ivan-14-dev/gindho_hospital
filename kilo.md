# Projet GinDHO - MVP Medical Assistance System

## Overview
GinDHO est un système d'assistance médicale complet développé en mode MVP (Minimum Viable Product) sur 10 jours.

## Architecture
- **Backend**: Spring Boot 3 + Java 21
- **Database**: PostgreSQL
- **Frontend Desktop**: JavaFX
- **Frontend Web**: React 18 + TypeScript
- **Security**: JWT + Spring Security
- **Real-time**: WebSocket STOMP

## Project Structure
```
GinDHO/
├── backend/              # Spring Boot Backend
│   ├── src/main/java/com/gindho/
│   │   ├── model/        # JPA Entities
│   │   ├── repository/   # Spring Data JPA Repositories
│   │   ├── service/      # Business Logic
│   │   ├── controller/   # REST Controllers
│   │   ├── security/     # JWT & Security
│   │   ├── scheduler/    # Background Tasks
│   │   ├── dto/          # Data Transfer Objects
│   │   └── config/       # Configuration
│   └── src/main/resources/
│       └── application.properties
├── javafx-client/        # JavaFX Desktop App
├── react-web/            # React Web App
└── Illustration_image/    # Architecture Diagrams
```

## Features
- User authentication (JWT)
- Role-based access control (Admin, Medecin, Patient, Utilisateur secondaire)
- Patient management
- Doctor scheduling
- Appointment booking with conflict detection
- Automatic email reminders
- Real-time notifications
- Medical record management

## Progress Tracking

### Étape 1 - Initialisation (Jour 1)
- ✅ Initialisation du monorepo
- ✅ Configuration Spring Boot 3.2.0
- ✅ Structure des dossiers
- ✅ Configuration Maven
- ✅ PostgreSQL configuration  
- ✅ Git repository initialized with main branch
- ✅ Remote origin configured
- ✅ README.md created
- ✅ .gitignore configured
- ✅ Application properties configured

### Étape 2 - Modélisation (Jour 2)
- ✅ BaseEntity with audit fields
- ✅ User entity with roles
- ✅ Patient entity
- ✅ Medecin entity
- ✅ RendezVous entity
- ✅ DossierMedical entity
- ✅ Ordonnance entity
- ✅ Disponibilite entity
- ✅ AuditLog entity
- ✅ RolePermission entity

### Étape 3 - Repository Layer (Jour 2)
- ✅ UserRepository
- ✅ PatientRepository
- ✅ MedecinRepository
- ✅ RendezVousRepository with conflict queries
- ✅ DossierMedicalRepository
- ✅ DisponibiliteRepository
- ✅ AuditLogRepository

### Étape 4 - Services & Authentication (Jour 3-4)
- ✅ JWT Service
- ✅ Custom UserDetailsService
- ✅ JWT Authentication Filter
- ✅ Security Configuration
- ✅ UserService with auth
- ✅ PatientService
- ✅ RendezVousService with conflict detection

### Étape 5 - REST API (Jour 5)
- ✅ Authentication endpoints
- ✅ Patient CRUD endpoints
- ✅ RendezVous endpoints
- ⚠️ Email endpoints to complete
- ⚠️ Report endpoints to complete

### Étape 6 - Advanced Features (Jour 6)
- ⚠️ RDV Scheduler to test
- ⚠️ Email Service to complete
- ✅ Conflict detection implemented

### Étape 7 - JavaFX Client (Jour 7)
- ✅ Initialisation du projet JavaFX
- ✅ Configuration Maven avec JavaFX 21
- ✅ Classe principale GinDhoClient
- ✅ Contrôleur de connexion (LoginController)
- ✅ Contrôleur de tableau de bord (DashboardController)
- ✅ Contrôleur de patients (PatientController)
- ✅ Contrôleur de rendez-vous (RendezVousController)
- ✅ Modèles de données (AuthResponse, LoginRequest)
- ✅ Service API HTTP
- ⚠️ Vues FXML à créer
- ⚠️ Intégration complète avec backend à terminer

### Étape 8 - React Web Client (Jour 8)
- ⏳ To be implemented

### Étape 9 - Integration (Jour 9)
- ⏳ To be completed

### Étape 10 - Finalization (Jour 10)
- ⏳ To be completed

## Database Schema
See class diagrams in Illustration_image/

## API Documentation
Swagger UI: http://localhost:8080/swagger-ui.html
API Docs: http://localhost:8080/v3/api-docs

## Running the Application
1. Start PostgreSQL database
2. Run backend: `mvn spring-boot:run` (backend directory)
3. Run JavaFX client: `mvn javafx:run` (javafx-client directory)
4. Run React app: `npm start` (react-web directory)

## Key Features Implemented
- ✅ Multi-layer architecture (Presentation, Business, Data)
- ✅ JWT-based authentication
- ✅ Role-based authorization
- ✅ Automatic conflict detection for appointments
- ✅ Scheduled email reminders
- ✅ WebSocket for real-time notifications
- ✅ Complete audit trail
- ✅ RESTful API design

## Next Steps
1. Complete JavaFX client implementation
2. Complete React web client
3. Add more comprehensive tests
4. Implement caching with Redis
5. Add PDF report generation
6. Dockerize the application

## Important Documents
- Backend Class Diagram: @Illustration_image/gindho_backend_class_diagram.html
- JavaFX Class Diagram: @Illustration_image/gindho_javafx_class_diagram.html
- System Architecture: @Illustration_image/gindho_system_architecture.svg
- Conception Documents: @Document_Conception/
