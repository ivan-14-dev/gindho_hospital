# Interconnect Service — Communication Inter-Hôpitaux Sécurisée

## Rôle

L'**interconnect-service** est le service transverse qui permet à **chaque hôpital/clinique du réseau GinDHO de communiquer directement et en toute sécurité** avec d'autres hôpitaux partenaires, sans passer systématiquement par l'Admin Zone.

Il agit comme un **hub de confiance décentralisé** pour les échanges inter-établissements.

## Architecture

```
┌─────────────────────┐          ┌─────────────────────┐
│   HÔPITAL A          │          │   HÔPITAL B          │
│                      │          │                      │
│  interconnect-svc ◄──┼────mTLS──┼──► interconnect-svc  │
│  (Port 9250)        │          │  (Port 9250)         │
│                      │          │                      │
│  POST /transfers     │          │  POST /public/       │
│  GET /partners       │          │    incoming-transfer │
│  POST /heartbeat     │          │                      │
└─────────────────────┘          └─────────────────────┘
         │                              │
         │         ┌──────────┐          │
         └─────────►   Kafka  ◄──────────┘
                   └──────────┘
```

## Fonctionnalités

### 1. Gestion des Partenaires
- **Enregistrement** : Un hôpital s'enregistre via `POST /public/register` (sans auth)
- **Activation** : Un administrateur active le partenaire via `PUT /partners/{id}/activate`
- **Suspension** : Suspension manuelle ou automatique après 5 heartbeat manqués
- **Découverte** : Liste des partenaires actifs disponible

### 2. Transfert de Patients
- **Initiation** : `POST /transfers` avec les données patient (chiffrées E2E)
- **Notification** : Appel HTTP direct à l'hôpital cible via `POST /api/interconnect/public/incoming-transfer`
- **Workflow** : PENDING → AWAITING_APPROVAL → APPROVED → IN_TRANSIT → COMPLETED
- **Consentement** : Traçabilité du consentement patient obligatoire

### 3. Heartbeat & Healthcheck
- **Heartbeat** : Chaque hôpital envoie un heartbeat toutes les 5 minutes
- **Détection** : 5 échecs consécutifs → suspension automatique
- **Statut online** : Visible en temps réel pour chaque partenaire

### 4. Partage de Données
- **Demande de données** : `POST /data-request` pour solliciter un dossier
- **Envoi direct** : Appel sécurisé à l'API publique de l'hôpital cible
- **Traçabilité** : Audit complet de tous les échanges

### 5. Sécurité
- **Authentification API Key** : Chaque hôpital a une API Key unique (`X-Hospital-API-Key`)
- **Authentification Hospital ID** : Couplé avec `X-Hospital-ID`
- **Chiffrement E2E** : Les données patient sont chiffrées avant envoi (préparation pour mTLS)
- **Audit trail** : Toutes les actions sont journalisées

## Endpoints API

### Public (sans authentification)
| Méthode | Path | Description |
|---------|------|-------------|
| POST | `/api/interconnect/public/register` | Enregistrer son hôpital |
| POST | `/api/interconnect/public/incoming-transfer` | Recevoir notification transfert |

### Authentifié (API Key requise)
| Méthode | Path | Description |
|---------|------|-------------|
| GET | `/api/interconnect/partners` | Lister tous les partenaires |
| GET | `/api/interconnect/partners/active` | Lister partenaires actifs |
| GET | `/api/interconnect/partners/{id}` | Détail d'un partenaire |
| GET | `/api/interconnect/partners/by-hospital/{hospitalId}` | Partenaire par hospitalId |
| PUT | `/api/interconnect/partners/{id}/activate` | Activer un partenaire |
| PUT | `/api/interconnect/partners/{id}/suspend` | Suspendre un partenaire |
| POST | `/api/interconnect/heartbeat` | Envoyer heartbeat |
| GET | `/api/interconnect/health/{hospitalId}` | Vérifier santé partenaire |
| POST | `/api/interconnect/transfers` | Initier un transfert |
| GET | `/api/interconnect/transfers/incoming` | Transferts entrants |
| GET | `/api/interconnect/transfers/outgoing` | Transferts sortants |
| GET | `/api/interconnect/transfers/patient/{patientId}` | Transferts d'un patient |
| GET | `/api/interconnect/transfers/{transferRef}` | Détail d'un transfert |
| PUT | `/api/interconnect/transfers/{id}/approve` | Approuver un transfert |
| PUT | `/api/interconnect/transfers/{id}/complete` | Compléter un transfert |
| PUT | `/api/interconnect/transfers/{id}/reject` | Rejeter un transfert |
| PUT | `/api/interconnect/transfers/{id}/acknowledge` | Accuser réception |
| GET | `/api/interconnect/transfers/pending-count` | Nombre de transferts en attente |
| POST | `/api/interconnect/data-request` | Demander données patient |

## Modèle de données

### HospitalPartner
| Champ | Type | Description |
|-------|------|-------------|
| hospitalId | String | Identifiant unique de l'hôpital |
| name | String | Nom de l'hôpital |
| baseUrl | String | URL de base pour les appels |
| apiKey | String (auto-généré) | Clé d'API secrète |
| trustLevel | TRUSTED/LIMITED/UNTRUSTED | Niveau de confiance |
| status | ACTIVE/SUSPENDED/PENDING_APPROVAL/DECOMMISSIONED | Statut |
| lastHeartbeatAt | DateTime | Dernier heartbeat |
| failedHeartbeats | int | Nb d'échecs consécutifs |

### InterHospitalTransfer
| Champ | Type | Description |
|-------|------|-------------|
| transferRef | String (auto-généré) | Référence unique TRF-XXXX |
| patientId | String | ID du patient |
| sourceHospitalId | String | Expéditeur |
| targetHospitalId | String | Destinataire |
| transferType | PATIENT_TRANSFER/EMERGENCY/CONSULTATION/etc. | Type |
| status | PENDING/APPROVED/COMPLETED/REJECTED/etc. | Statut workflow |
| encryptedPatientData | TEXT | Données patient chiffrées |

## Déploiement

```bash
# Build
cd services/interconnect-service
mvn clean package

# Run (dev)
mvn spring-boot:run

# Docker
docker build -t gindho/interconnect-service .
docker run -p 9250:9250 gindho/interconnect-service

# Base de données
createdb gindho_interconnect
```

## Exemple d'utilisation

```bash
# 1. Hôpital A s'enregistre
curl -X POST http://localhost:9250/api/interconnect/public/register \
  -H "Content-Type: application/json" \
  -d '{
    "hospitalId": "HOSP-A",
    "name": "Hôpital Central A",
    "baseUrl": "http://hospital-a.internal:9250",
    "contactEmail": "contact@hopital-a.com"
  }'

# 2. Hôpital B s'enregistre
curl -X POST http://localhost:9250/api/interconnect/public/register \
  -H "Content-Type: application/json" \
  -d '{
    "hospitalId": "HOSP-B",
    "name": "Hôpital Central B",
    "baseUrl": "http://hospital-b.internal:9250",
    "contactEmail": "contact@hopital-b.com"
  }'

# 3. Transfert patient de HOSP-A vers HOSP-B
curl -X POST http://localhost:9250/api/interconnect/transfers \
  -H "Content-Type: application/json" \
  -H "X-Hospital-API-Key: <api-key-a>" \
  -H "X-Hospital-ID: HOSP-A" \
  -d '{
    "patientId": "PAT-123",
    "patientName": "Jean Dupont",
    "targetHospitalId": "HOSP-B",
    "transferType": "PATIENT_TRANSFER",
    "reason": "Besoin spécialiste cardiologue",
    "consentObtained": true
  }'