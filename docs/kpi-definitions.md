# GinDHO — KPI Définitions

> Mesures clés de performance pour le pilotage du système.
> Documenté selon PLAN_AMELIORATION_SYSTEME.md Phase 0.

## KPI Techniques

| KPI | Cible | Mesure | Source |
|---|---|---|---|
| **MTTR** (Mean Time To Recover) | < 30 min | Temps entre la détection d'un incident et le retour à la normale | PagerDuty/Opsgenie |
| **p95 latence API** | < 500 ms | 95e percentile du temps de réponse des 10 endpoints les plus appelés | Prometheus + Grafana |
| **p99 latence API** | < 2 s | 99e percentile pour les endpoints critiques | Prometheus + Grafana |
| **Disponibilité** | 99.9 % | Uptime des services critiques (patient, appointment, billing) | Prometheus + Blackbox Exporter |
| **Couverture tests** | ≥ 70 % lignes | Ratio lignes couvertes / lignes totales | JaCoCo + SonarQube |
| **Couverture sécurité** | ≥ 80 % | Couverture sur les modules security/, service/ | JaCoCo + SonarQube |
| **Vulnérabilités CVE** | 0 Critical/High | Nombre de vulnérabilités ouvertes > Medium | Trivy + Snyk |
| **Dette technique** | < 5 % | Ratio code dupliqué / code total | SonarQube |
| **Secrets dans le repo** | 0 | Détecté par gitleaks en CI | Gitleaks |

## KPI Métier

| KPI | Cible | Mesure | Domaine |
|---|---|---|---|
| **RDV/h** | Variable | Nombre de rendez-vous par heure par médecin | appointment-service |
| **Admissions/jour** | Variable | Nombre d'admissions quotidiennes | admission-service |
| **Taux d'occupation lits** | < 85 % | Ratio lits occupés / lits disponibles | ward-service |
| **Temps d'attente urgence** | < 30 min | Temps moyen entre arrivée et prise en charge | emergency-service |
| **Délai résultats labo** | < 4 h | Temps entre prélèvement et résultat | laboratory-service |
| **Taux d'impayés** | < 5 % | Ratio factures impayées / total | billing-service |
| **Satisfaction patient** | > 4/5 | Score moyen des enquêtes | reporting-service |

## SLO/SLI par Service

| Service | SLO | SLI | Fenêtre |
|---|---|---|---|
| patient-service | 99.9 % | Disponibilité HTTP 200 | 30 jours |
| appointment-service | 99.5 % | Requêtes réussies / total | 30 jours |
| billing-service | 99.9 % | Transactions réussies | 30 jours |
| emergency-service | 99.99 % | Temps de réponse < 200 ms | 7 jours |
| audit-service | 99.99 % | Écriture Kafka réussie | 7 jours |

## Alerting

| Seuil | Action | Canal |
|---|---|---|
| p95 > 1 s | Alerte warning | Slack #alerts |
| p95 > 2 s | Alerte critique | PagerDuty |
| Erreur 5xx > 1 % | Alerte critique | PagerDuty |
| Couverture < 60 % | Bloquant CI | SonarQube Gate |
| CVE Critical > 0 | Bloquant CI | Trivy |
