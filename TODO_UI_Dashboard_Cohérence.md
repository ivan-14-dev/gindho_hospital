# TODO — Cohérence UI “Dashboard” (JavaFX + style unique)

## Objectif
Appliquer sur **toutes** les pages JavaFX un style cohérent inspiré du design fourni :
- Sidebar noire (narrow/rounded)
- Topbar blanche + recherche + avatar
- Cards blanches / arrondies
- Accent orange **#F5A623**
- Cohérence d’espacement, typographies, boutons (hover/active)

## Statut global
- [x] main-dashboard.fxml + dashboard.css (base inspirée DashboardExact) — en cours d’affinage
- [ ] Harmonisation progressive des autres vues

---

## Checklist par étape

### 1) Fondations (CSS)
- [x] Etendre `javafx-client/src/main/resources/styles/dashboard.css` (classes utilisées par main-dashboard.fxml)
- [ ] Normaliser les styles communs : `.card`, `.card-title`, `.btn-primary`, `.btn-ghost`, `.topbar`, `.sidebar-nav-btn`, etc.
- [ ] Vérifier interaction hover/active/disabled cohérente sur tous les boutons
- [ ] Décliner darkMode (en restant cohérent, sans casser les overlays existants)

### 2) Validation FXML / imports
- [x] Corriger `main-dashboard.fxml` (imports `javafx.scene.shape.*`)
- [ ] Contrôler que chaque FXML ciblée compile (imports + types)
- [ ] Contrôler les `fx:id` nécessaires aux controllers (ne pas casser la logique)

### 3) Application par page (ordre recommandé)
> Prochaine priorité : pages “table” + pages de navigation (elles donnent le plus gros ressenti visuel)

- [x] `rendezvous.fxml`
- [x] `upcoming-appointments.fxml`
- [x] `analyses.fxml`
- [x] `medecin.fxml`
- [x] `patients.fxml` / `patient.fxml` (selon votre routing)
- [x] `prescriptions.fxml`
- [x] `hospitalisations.fxml`
- [x] `disponibilites.fxml`
- [x] `stats.fxml`
- [x] `revenus.fxml`
- [x] `audit-logs.fxml`
- [x] `settings.fxml`
- [x] `admin-users.fxml`
- [x] `dossier-medical.fxml`
- [x] `forgot-password.fxml` / autres écrans auth si nécessaire

### 4) Améliorations (qualité visuelle)
- [ ] Ajouter des micro-animations : hover, focus, transitions (subtiles)
- [ ] Uniformiser les grilles / paddings (ex: 12/16/24)
- [ ] Uniformiser la largeur des charts/cards
- [ ] Corriger tout overflow / clipping sur petites résolutions

---

## Notes
- Ne pas faire de changement “fonctionnel” dans les controllers pendant la phase UI (uniquement style + layout).
- Toute modification d’un FXML doit préserver les `fx:id` utilisés.
