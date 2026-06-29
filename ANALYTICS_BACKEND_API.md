# GinDHO Analytics - Documentation API Backend

## Vue d'ensemble

Ce document décrit tous les endpoints API nécessaires pour alimenter le module Analytics frontend.

## Endpoints Principaux

### 1. Executive Dashboard

#### GET /analytics-service/executive-metrics
Récupère les KPIs de la vue exécutive.

**Paramètres:**
- `startDate` (string): Date de début (YYYY-MM-DD)
- `endDate` (string): Date de fin (YYYY-MM-DD)
- `period` (string): Période (day|week|month|year|custom)
- `department` (string): Filtre par département (optionnel)

**Réponse:**
```json
{
  "totalPatients": 1234,
  "consultationsToday": 156,
  "admissions": 28,
  "discharges": 12,
  "hospitalized": 456,
  "bedsAvailable": 344,
  "occupancyRate": 87,
  "waitTime": 24,
  "revenue": 150000,
  "expenses": 100000,
  "surgeries": 145,
  "satisfaction": 92
}
```

#### GET /analytics-service/executive-charts
Récupère les données des graphiques de la vue exécutive.

**Réponse:**
```json
{
  "patientsEvolution": [
    { "name": "Jan", "value": 1100, "date": "2024-01-01" },
    { "name": "Fév", "value": 1150, "date": "2024-02-01" }
  ],
  "revenueExpense": [
    { "name": "Jan", "revenue": 145000, "expenses": 95000 },
    { "name": "Fév", "revenue": 150000, "expenses": 100000 }
  ],
  "serviceDistribution": [
    { "name": "Urgences", "value": 456 },
    { "name": "Chirurgie", "value": 234 }
  ],
  "bedOccupancy": [
    { "name": "Jour 1", "occupied": 300, "available": 200 },
    { "name": "Jour 2", "occupied": 310, "available": 190 }
  ]
}
```

### 2. Patients Analytics

#### GET /analytics-service/patients-metrics
Métriques des patients.

**Réponse:**
```json
{
  "activePatients": 1234,
  "newPatients": 45,
  "followUp": 234,
  "retentionRate": 85
}
```

#### GET /analytics-service/patients-charts
Graphiques de l'analyse patients.

**Réponse:**
```json
{
  "ageDistribution": [
    { "name": "0-18", "value": 120 },
    { "name": "18-35", "value": 345 }
  ],
  "genderDistribution": [
    { "name": "Homme", "value": 620 },
    { "name": "Femme", "value": 614 }
  ],
  "admissionTrend": [
    { "name": "Sem 1", "value": 45 },
    { "name": "Sem 2", "value": 52 }
  ]
}
```

### 3. Consultations Analytics

#### GET /analytics-service/consultations-metrics
Métriques des consultations.

#### GET /analytics-service/consultations-charts
Graphiques des consultations.

### 4. Hospitalisations Analytics

#### GET /analytics-service/hospitalizations-metrics
Métriques des hospitalisations.

#### GET /analytics-service/hospitalizations-charts
Graphiques des hospitalisations.

### 5. Surgery Analytics

#### GET /analytics-service/surgery-metrics
Métriques du bloc opératoire.

#### GET /analytics-service/surgery-charts
Graphiques du bloc opératoire.

### 6. Imaging Analytics

#### GET /analytics-service/imaging-metrics
Métriques de radiologie.

#### GET /analytics-service/imaging-charts
Graphiques de radiologie.

### 7. Blood Bank Analytics

#### GET /analytics-service/bloodbank-metrics
Métriques de la banque de sang.

### 8. HR Analytics

#### GET /analytics-service/hr-metrics
Métriques RH.

### 9. Quality Analytics

#### GET /analytics-service/quality-metrics
Métriques qualité.

### 10. Epidemiology Analytics

#### GET /analytics-service/epidemiology-metrics
Métriques épidémiologie.

### 11. Satisfaction Analytics

#### GET /analytics-service/satisfaction-metrics
Métriques satisfaction.

### 12. Reports Management

#### GET /analytics-service/reports-metrics
Métriques des rapports.

#### GET /analytics-service/reports
Récupère la liste des rapports disponibles.

#### POST /analytics-service/reports/generate
Génère un nouveau rapport.

**Paramètres:**
```json
{
  "type": "monthly|annual|custom",
  "filters": {
    "startDate": "2024-01-01",
    "endDate": "2024-06-30",
    "sections": ["executive", "patients", "finance"]
  }
}
```

#### GET /analytics-service/reports/:id/download
Télécharge un rapport généré.

## Paramètres Globaux Supportés

Tous les endpoints supportent ces paramètres de filtrage:

| Paramètre | Type | Description |
|-----------|------|-------------|
| `startDate` | string | Date de début (YYYY-MM-DD) |
| `endDate` | string | Date de fin (YYYY-MM-DD) |
| `period` | string | Période (day, week, month, year, custom) |
| `department` | string | Filtre par département |
| `specialty` | string | Filtre par spécialité |
| `doctorId` | string | Filtre par médecin |
| `gender` | string | Filtre par sexe (M, F, Other) |

## Formats de Réponse

### Succès (200)
```json
{
  "success": true,
  "data": { ... },
  "meta": {
    "timestamp": "2024-06-29T10:30:00Z",
    "version": "1.0"
  }
}
```

### Erreur (4xx/5xx)
```json
{
  "success": false,
  "error": {
    "code": "INVALID_PARAMS",
    "message": "Les paramètres fournis sont invalides"
  }
}
```

## Authentification

Tous les endpoints nécessitent:
- Header: `Authorization: Bearer {token}`
- Header: `Content-Type: application/json`

## Pagination

Pour les endpoints retournant des listes:

```json
{
  "data": [...],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "total": 150,
    "pages": 8
  }
}
```

## Caching

Recommandations de caching pour React Query:

```typescript
useQuery({
  queryKey: ['analytics', section, dateRange],
  queryFn: async () => { ... },
  staleTime: 5 * 60 * 1000,      // 5 minutes
  cacheTime: 10 * 60 * 1000,     // 10 minutes
  refetchInterval: 30 * 60 * 1000 // Rafraîchir tous les 30 min
})
```

## Limitations

- Taille max des réponses: 50MB
- Délai max par requête: 30 secondes
- Rate limit: 100 requêtes/minute par utilisateur
- Historique gardé: 2 ans

## Implémentation Frontend

Les composants utilisent déjà les appels API:

```typescript
// Exemple dans ExecutiveDashboard.tsx
const { data: metrics = {} } = useQuery({
  queryKey: ['executive-metrics', dateRange, period, filters],
  queryFn: async () => {
    const response = await apiClient.get('/analytics-service/executive-metrics', {
      params: {
        startDate: dateRange.start,
        endDate: dateRange.end,
        period,
        ...filters,
      },
    });
    return response.data || {};
  },
});
```

## Status de l'Implémentation

| Endpoint | Status | Données | Graphiques |
|----------|--------|---------|-----------|
| Executive | ✅ | Stub Data | Recharts |
| Patients | ✅ | Stub Data | Recharts |
| Consultations | ✅ | Stub Data | Recharts |
| Hospitalisations | ✅ | Stub Data | Recharts |
| Chirurgie | ✅ | Stub Data | Recharts |
| Radiologie | ✅ | Stub Data | Recharts |
| Banque de Sang | ✅ | Stub Data | - |
| RH | ✅ | Stub Data | - |
| Qualité | ✅ | Stub Data | - |
| Épidémiologie | ✅ | Stub Data | - |
| Satisfaction | ✅ | Stub Data | - |
| Rapports | ✅ | Stub Data | - |

## Notes de Développement

1. Tous les endpoints retournent des données en UTC
2. Les dates sont au format ISO 8601
3. Les nombres décimaux utilisent le point (.) comme séparateur
4. Les pourcentages sont des nombres (0-100)
5. Les durées sont en minutes

## Exemples de Requêtes

### Récupérer les métriques du mois courant
```bash
curl -X GET \
  'http://localhost:3000/analytics-service/executive-metrics?startDate=2024-06-01&endDate=2024-06-30&period=month' \
  -H 'Authorization: Bearer {token}' \
  -H 'Content-Type: application/json'
```

### Filtrer par département
```bash
curl -X GET \
  'http://localhost:3000/analytics-service/consultations-metrics?startDate=2024-06-01&endDate=2024-06-30&department=urgences' \
  -H 'Authorization: Bearer {token}'
```

### Générer un rapport personnalisé
```bash
curl -X POST \
  'http://localhost:3000/analytics-service/reports/generate' \
  -H 'Authorization: Bearer {token}' \
  -H 'Content-Type: application/json' \
  -d '{
    "type": "custom",
    "filters": {
      "startDate": "2024-01-01",
      "endDate": "2024-06-30",
      "sections": ["executive", "patients", "finance"]
    }
  }'
```
