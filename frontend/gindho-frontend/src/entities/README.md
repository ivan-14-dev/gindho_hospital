# Entities Layer

Responsabilite:

- Modeles metier frontend.
- Schemas runtime Zod.
- Mappers DTO vers modeles UI.
- Types alignes sur les OpenAPI microservices.

Contrats sources:

- `../../GinDHO_Hospital/docs/api/gindho-openapi.yaml`
- `../../GinDHO_Hospital/docs/api/patient-service-openapi.yaml`

Regles:

- Une entite ne doit pas appeler l'API.
- Les DTO sensibles doivent rester explicites.
- Les differences entre API et UI doivent passer par un mapper teste.
