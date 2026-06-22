# Politique IA - GinDHO

## Principes de sécurité

### Données interdites (PHI - Protected Health Information)
- Numéros de sécurité sociale
- Adresses postales complètes
- Numéros de téléphone
- Données de facturation détaillées
- Notes psychiatriques
- Résultats d'analyses génétiques
- Informations sur les traitements contre indications

### Principes d'IA responsable
1. L'IA ne peut jamais accéder directement aux données patients
2. Les résumés médicaux doivent être anonymisés avant tout traitement
3. L'IA sert d'assistant, jamais de décision médicale autonome
4. Toute interaction IA est auditée et tracée

### Validation
- Aucun envoi de PHI vers un service IA externe
- Les prompts sont sanitarisés côté client
- Le backend valide les données avant tout traitement

### Consentement
- Les patients doivent donner leur consentement explicite
- Le consentement est stocké dans leur dossier
- Possibilité de révoquer à tout moment