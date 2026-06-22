# TODO — Améliorations UX JavaFX (GinDHO)

## Objectif
Rendre l’UI plus cohérente, rassurante et accessible : RBAC visible, feedback clair, états vides pendant chargements, validations/formulaires plus robustes.

---

## Phase 0 — Hygiene rapide (déjà commencé)
- [x] Supprimer les `showAlert("DEBUG", ...)` involontaires (ex: `MedecinsController`)
- [x] Restaurer/valider `MainDashboardController.java` après une corruption de fichier
- [x] S’assurer que le build JavaFX fonctionne (`mvn ... compile/package`)

---

## Phase 1 — Dashboard (navigation + perception RBAC + dark mode + états)
### 1.1 RBAC côté UI (visibilité ET comportement)
- [ ] Dans `MainDashboardController.setupNavigation()` :
  - [ ] Masquer/afficher précisément `auditLogsBtn`, `hospitalisationsBtn`, `adminUsersBtn`
  - [ ] Masquer/afficher correctement les entrées selon `hasDynamicPermission("resource","READ")` (et ADMIN/SUPER_ADMIN)
- [ ] Dans `MainDashboardController.handleAuditLogs/handleHospitalisations/handleAdminUsers()` :
  - [ ] Bloquer avec message clair si l’utilisateur tente un accès via raccourci/état persistant

### 1.2 Dark mode (cohérence + persistance)
- [ ] Dans `MainDashboardController` :
  - [ ] Appliquer/retirer `dark-theme.css` de façon fiable (pas de doublons de stylesheets)
  - [ ] Initialiser le toggle avec l’état courant de la scène (si besoin)
- [ ] Option (si faisable) : mémoriser préférence en session (ex: `GinDhoClient`/`Session`)

### 1.3 États vides + chargement
- [ ] Dans `MainDashboardController.loadDashboardStats()` et widgets :
  - [ ] Afficher un placeholder “Chargement…” le temps du fetch
  - [ ] Afficher “Aucune donnée disponible” si API renvoie empty/null (au lieu d’un espace vide)
  - [ ] Éviter les silencieux `catch (Exception ignored)` : afficher une erreur UX non bloquante (ou au minimum `Alert`)

---

## Phase 2 — CRUD “tables” (Patients, Médecins, Analyses, etc.)
Pour chaque contrôleur table/list :
- [ ] Ajouter un état “Chargement…” (overlay ou label) pendant :
  - [ ] `loadXxx()`, `searchXxx()`, `refresh()`
- [ ] Gérer “aucune ligne” :
  - [ ] Message explicite (ex: “Aucun patient ne correspond à votre recherche”)
- [ ] Gérer erreurs API :
  - [ ] Message utilisateur + conserver l’ancienne table au lieu de vider brutalement
- [ ] Boutons désactivés pendant requêtes :
  - [ ] `refreshBtn`, `addBtn`, `save` dans dialogs (si applicable)

## Phase 3 — Dialogs/Forms (validation + confirmations)
### 3.1 Validation champs requis
- [ ] Dans les formulaires (ex `MedecinsController` / `MedecinForm`) :
  - [ ] Vérifier champs requis avant d’appeler l’API
  - [ ] Messages de validation compréhensibles (pas seulement exception `ex.getMessage()`)

### 3.2 Confirmation cohérente
- [ ] Unifier la logique “Supprimer / Modifier / Créer” :
  - [ ] Confirmation avant suppression
  - [ ] Retour de résultat cohérent après modification
  - [ ] Éviter les Optional null/empty incohérents

---

## Phase 4 — Feedback global (UX non bloquante)
- [ ] Remplacer progressivement les `Alert` bloquantes par un système non bloquant :
  - [ ] Toast/snackbar (si on implémente une petite UI utilitaire)
  - [ ] Sinon : au minimum harmoniser titres/messages et limiter les modales

---

## Phase 5 — Accessibilité & micro-UX
- [ ] S’assurer que chaque bouton a un label clair
- [ ] Réduire les cas où l’utilisateur peut cliquer avant la fin du chargement
- [ ] Harmoniser textes (FR) : cohérence “Rôle : …”, “Permission requise : …”, etc.
