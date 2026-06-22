# GinDHO API Documentation

## Architecture

GinDHO est composé de **30 microservices** indépendants. Chaque service a sa propre API REST documentée via Swagger UI.

## Accès Swagger UI

Chaque service expose Swagger UI sur son port :
| Service | Port | URL |
|---------|------|-----|
| Patient Service | 8081 | http://localhost:8081/swagger-ui.html |
| Appointment Service | 8082 | http://localhost:8082/swagger-ui.html |
| Medical Record Service | 8083 | http://localhost:8083/swagger-ui.html |
| Billing Service | 8084 | http://localhost:8084/swagger-ui.html |
| Pharmacy Service | 8085 | http://localhost:8085/swagger-ui.html |
| Laboratory Service | 8086 | http://localhost:8086/swagger-ui.html |
| Admission Service | 8087 | http://localhost:8087/swagger-ui.html |
| Emergency Service | 8088 | http://localhost:8088/swagger-ui.html |
| Authorization Service | 8089 | http://localhost:8089/swagger-ui.html |
| Identity Service | 8090 | http://localhost:8090/swagger-ui.html |

Ou via l'API Gateway :
| Gateway | URL |
|---------|-----|
| Kong | http://localhost:8000/services/{service}/swagger-ui.html |

## Authentification

### Obtenir un token
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

### Utiliser le token
```bash
GET /api/patients
Authorization: Bearer <token>
```

## Permissions

Chaque endpoint nécessite une permission spécifique :

| Module | Permissions | Description |
|--------|-------------|-------------|
| PATIENT | READ, WRITE, DELETE | Gestion des patients |
| APPOINTMENT | READ, WRITE, DELETE | Gestion des rendez-vous |
| EMR | READ, WRITE | Dossier médical |
| BILLING | READ, WRITE, DELETE | Facturation |
| PHARMACY | READ, WRITE | Pharmacie |
| LAB | READ, WRITE | Laboratoire |
| ADMISSION | READ, WRITE | Hospitalisation |
| EMERGENCY | READ, WRITE | Urgences |
| ADMIN | USER, ROLE, AUDIT, CONFIG | Administration |

## Endpoints principaux

### Patient Service (`/api/patients`)
- `GET /api/patients` - Liste paginée des patients
- `GET /api/patients/{id}` - Détail d'un patient
- `POST /api/patients` - Créer un patient
- `PUT /api/patients/{id}` - Modifier un patient
- `DELETE /api/patients/{id}` - Supprimer un patient
- `GET /api/patients/search?query=` - Rechercher des patients
- `GET /api/patients/{id}/contacts` - Contacts d'un patient
- `GET /api/patients/{id}/assurances` - Assurances d'un patient
- `GET /api/patients/{id}/documents` - Documents d'un patient

### Appointment Service (`/api/appointments`)
- `GET /api/appointments` - Liste des rendez-vous
- `POST /api/appointments` - Créer un rendez-vous
- `PUT /api/appointments/{id}/confirm` - Confirmer un rendez-vous
- `PUT /api/appointments/{id}/cancel` - Annuler un rendez-vous
- `GET /api/appointments/medecin/{medecinId}` - RDV d'un médecin
- `GET /api/appointments/patient/{patientId}` - RDV d'un patient

### Authorization Service (`/api/authorization`)
- `GET /api/authorization/check?permission=` - Vérifier une permission
- `GET /api/authorization/permissions` - Permissions de l'utilisateur
- `POST /api/authorization/roles` - Créer un rôle
- `POST /api/authorization/permissions` - Créer une permission
- `POST /api/authorization/assign` - Assigner un rôle à un utilisateur

### Identity Service (`/api/auth`)
- `POST /api/auth/login` - Connexion
- `POST /api/auth/refresh` - Rafraîchir le token
- `GET /api/auth/me` - Informations utilisateur courant

### Billing Service (`/api/billing`)
- `GET /api/billing/invoices` - Liste des factures
- `POST /api/billing/invoices` - Créer une facture
- `PUT /api/billing/invoices/{id}/pay` - Payer une facture

### Payment Service (`/api/payments`)
- `POST /api/payments` - Effectuer un paiement
- `POST /api/payments/{id}/refund` - Rembourser
- `GET /api/payments/invoice/{invoiceId}` - Paiements d'une facture

### Pharmacy Service (`/api/pharmacy`)
- `GET /api/pharmacy/medicaments` - Liste des médicaments
- `POST /api/pharmacy/medicaments` - Ajouter un médicament
- `GET /api/pharmacy/lots` - Lots de médicaments
- `GET /api/pharmacy/lots/perimes` - Lots périmés
- `GET /api/pharmacy/prescriptions/patient/{patientId}` - Ordonnances d'un patient

### Laboratory Service (`/api/laboratory`)
- `GET /api/laboratory/analyses` - Liste des analyses
- `POST /api/laboratory/analyses` - Prescrire une analyse
- `PUT /api/laboratory/analyses/{id}/result` - Saisir un résultat
- `PUT /api/laboratory/analyses/{id}/validate` - Valider un résultat

### Admission Service (`/api/admissions`)
- `POST /api/admissions` - Admettre un patient
- `PUT /api/admissions/{id}/discharge` - Sortie d'un patient
- `GET /api/admissions/patient/{patientId}` - Admissions d'un patient

### Emergency Service (`/api/emergency`)
- `POST /api/emergency/triage` - Enregistrer un triage
- `GET /api/emergency/triage` - Liste des urgences
- `PUT /api/emergency/triage/{id}/take-charge` - Prise en charge

### Audit Service (`/api/audit`)
- `GET /api/audit` - Journal des actions
- `GET /api/audit/user/{userId}` - Actions d'un utilisateur
- `GET /api/audit/module/{module}` - Actions par module

### Notification Service
- Écoute les événements Kafka : `patient`, `notification`
- Envoie via le canal configuré (SMS, Email, Push)

## Événements Kafka

| Topic | Événements | Producteur | Consommateurs |
|-------|-----------|------------|---------------|
| patient | PatientCreated, PatientUpdated | patient-service | notification, audit |
| appointment | AppointmentBooked, AppointmentConfirmed | appointment-service | notification, audit |
| billing | InvoiceCreated, InvoicePaid | billing-service | notification, audit, reporting |
| notification | NotificationRequest | notification-service | - (consommé localement) |
| audit | AuditEvent | tous les services | audit-service |
| medical | MedicalRecordCreated, PrescriptionCreated | medical-record, pharmacy | audit |
| inventory | StockLow, StockOut | inventory-service | notification |
| emergency | EmergencyAdmitted, EmergencyReleased | emergency-service | notification, admission |

## Frontend

### React Web
- URL: http://localhost:3000
- Composants dynamiques générés à partir des fichiers de configuration UI
- Les sections, widgets et boutons sont filtrés selon les permissions

### JavaFX
- Application desktop autonome
- Mêmes fichiers de configuration UI que le Web
- Layout généré dynamiquement selon le rôle
