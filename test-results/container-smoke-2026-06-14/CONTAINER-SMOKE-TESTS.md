# Tests conteneurs sequentiels GinDHO

Date: 2026-06-14
Mode: smoke test Docker; un microservice applicatif a la fois; dependances partagees PostgreSQL/Kafka; validation via GET /actuator/health.

Variables de test: SERVER_PORT=8080, SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/<service>, SPRING_JPA_HIBERNATE_DDL_AUTO=update, KAFKA_BOOTSTRAP_SERVERS=kafka:29092.

| Service | Image | Resultat | HTTP | Duree | Note |
|---|---|---:|---:|---:|---|
| admission-service | gindho/admission-service:latest | PASSED | 200 | 26s | /actuator/health OK |
| ambulance-service | gindho/ambulance-service:latest | PASSED | 200 | 27s | /actuator/health OK |
| api-gateway | gindho/api-gateway:latest | PASSED | 200 | 125s | /actuator/health OK |
| appointment-service | gindho/appointment-service:latest | PASSED | 200 | 31s | /actuator/health OK |
| asset-service | gindho/asset-service:latest | PASSED | 200 | 27s | /actuator/health OK |
| audit-service | gindho/audit-service:latest | PASSED | 200 | 22s | /actuator/health OK |
| authorization-service | gindho/authorization-service:latest | FAILED | 000 | 6s | container exited |
| bed-service | gindho/bed-service:latest | PASSED | 200 | 26s | /actuator/health OK |
| billing-service | gindho/billing-service:latest | PASSED | 200 | 27s | /actuator/health OK |
| emergency-service | gindho/emergency-service:latest | PASSED | 200 | 21s | /actuator/health OK |
| event-service | gindho/event-service:latest | PASSED | 200 | 26s | /actuator/health OK |
| hr-service | gindho/hr-service:latest | PASSED | 200 | 22s | /actuator/health OK |
| identity-service | gindho/identity-service:latest | FAILED | 000 | 16s | container exited |
| imaging-service | gindho/imaging-service:latest | PASSED | 200 | 22s | /actuator/health OK |
| insurance-service | gindho/insurance-service:latest | PASSED | 200 | 21s | /actuator/health OK |
| inventory-service | gindho/inventory-service:latest | PASSED | 200 | 26s | /actuator/health OK |
| laboratory-service | gindho/laboratory-service:latest | PASSED | 200 | 26s | /actuator/health OK |
| medical-record-service | gindho/medical-record-service:latest | PASSED | 200 | 22s | /actuator/health OK |
| notification-service | gindho/notification-service:latest | PASSED | 200 | 22s | /actuator/health OK |
| patient-service | gindho/patient-service:latest | FAILED | 000 | 21s | container exited |
| payment-service | gindho/payment-service:latest | PASSED | 200 | 21s | /actuator/health OK |
| pharmacy-service | gindho/pharmacy-service:latest | PASSED | 200 | 21s | /actuator/health OK |
| prescription-service | gindho/prescription-service:latest | PASSED | 200 | 21s | /actuator/health OK |
| procurement-service | gindho/procurement-service:latest | PASSED | 200 | 21s | /actuator/health OK |
| reporting-service | gindho/reporting-service:latest | PASSED | 200 | 21s | /actuator/health OK |
| round-service | gindho/round-service:latest | PASSED | 200 | 26s | /actuator/health OK |
| scheduling-service | gindho/scheduling-service:latest | PASSED | 200 | 21s | /actuator/health OK |
| surgery-service | gindho/surgery-service:latest | PASSED | 200 | 21s | /actuator/health OK |
| ward-service | gindho/ward-service:latest | PASSED | 200 | 26s | /actuator/health OK |
