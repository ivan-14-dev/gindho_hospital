# API Contracts - Dashboard Service

## reporting-service Endpoints (via Kong API Gateway)

### Authentication Required
All endpoints require `Authorization: Bearer <token>` header.

### Endpoints

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/dashboard/stats` | GET | ADMIN, MEDECIN, NURSE, RECEPTION | Statistiques principales |
| `/api/dashboard/recent-activity` | GET | ADMIN, MEDECIN, NURSE, RECEPTION | Activité récente |

### Requests

```http
GET /api/dashboard/stats
Authorization: Bearer <token>
```

### Responses

```json
{
  "content": {
    "totalPatients": 1234,
    "totalRendezVous": 56,
    "totalConsultations": 89,
    "totalHospitalisations": 23,
    "rendezVousAujourdhui": 12,
    "patientsParMois": [{"mois": "2024-01", "count": 120}],
    "rendezVousParStatut": [{"statut": "PLANIFIE", "count": 30}]
  }
}
```

### Error Responses

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token expired",
  "path": "/api/dashboard/stats"
}
```

### Rate Limiting
- 100 req/min par utilisateur
- 429 retourné si dépassé

### WebSocket Events
Si disponible via `/api/ws`:
- `dashboard.stats.updated` - Stats mises à jour
- `dashboard.activity.created` - Nouvelle activité