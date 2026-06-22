# Services Layer

Responsabilite:

- Clients API microservices.
- Gestion des erreurs HTTP.
- Validation des reponses.
- Integration avec TanStack Query.

Contrats sources:

- Kong local: `http://localhost:8000`
- Production: `https://api.gindho.com`
- OpenAPI global: `../../GinDHO_Hospital/docs/api/gindho-openapi.yaml`
- OpenAPI patient-service: `../../GinDHO_Hospital/docs/api/patient-service-openapi.yaml`

Regles:

- Aucun service ne cible le backend monolithique.
- Les nouveaux endpoints doivent venir de la gateway microservices.
- Les endpoints versionnes `/api/v1/...` sont preferes pour les nouveaux modules.
- Les erreurs 401/403 doivent remonter vers la couche auth globale.
