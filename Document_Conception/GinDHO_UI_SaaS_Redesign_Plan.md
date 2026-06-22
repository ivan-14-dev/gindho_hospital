# GinDHO — Plan d’amélioration du client JavaFX (style SaaS “Gestion d’hôpitaux”)

## Objectif
Rendre l’interface JavaFX plus proche des écrans modernes “SaaS” : cohérence visuelle, hiérarchie claire, composants réutilisables, espaces homogènes, tableaux et formulaires plus “product-level”, tout en gardant une identité graphique simple (contraste + sobriété).

## Constat actuel (d’après le CSS/FXML)
- Thème existant **brutalist** :
  - `javafx-client/src/main/resources/styles/style.css` : thème sombre pour login + composants.
  - `javafx-client/src/main/resources/styles/dashboard.css` : thème clair pour dashboard.
- Plusieurs écrans “stub” utilisent des **styles inline** (patient/medecin/rendezvous/prescriptions/revenus).
- Le dashboard (`main-dashboard.fxml`) a un layout global correct (sidebar + content), mais :
  - il manque une **barre supérieure (topbar)** et des **patterns SaaS** réutilisables (badges, empty state, toolbar cohérente).
  - la gestion des composants (cartes/statuts/controles) n’est pas entièrement standardisée.
- `analyses.fxml` charge `style.css` (thème global brut) et pas forcément le thème dashboard, ce qui crée un risque de **désalignement** de style.

---

## Principes de design SaaS à appliquer
1. **Tokens CSS** (couleurs/typos/espacements) : remplacer la duplication de valeurs (ex: `#111111`, `#F5F5F7`, bordures).
2. **Hiérarchie typographique** stable :
   - titres de page
   - sous-titres
   - labels
   - texte secondaire
3. **Composants cohérents** :
   - boutons primaires/secondaires (hover/disabled)
   - champs de formulaire (focus, error)
   - cartes “surface” (bordure/ombre légère, rayon cohérent)
   - badges (statuts : urgent/actif/inactif)
   - tableaux (entêtes, lignes, hover, sélection, empty state)
4. **Suppression des styles inline** :
   - préférer `styleClass` + CSS unique.
5. **Topbar + contenu standardisé** :
   - topbar avec recherche/notifications/profil
   - zone content avec padding cohérent
6. **Accessibilité & utilisabilité** :
   - focus visible sur clavier
   - tailles de boutons confortables
   - éviter le texte coupé (padding, preferred sizes).

---

## Plan d’amélioration (priorités)

### Phase 1 — Harmonisation du thème (P1)
**But :** unifier le look & feel sur toutes les vues.

**Actions**
1. Créer un fichier CSS “tokens” et le réutiliser partout :
   - nouvelles variables CSS (JavaFX CSS supporte les couleurs via `-fx-*`, mais on peut aussi centraliser par classes et valeurs homogènes).
   - harmoniser le contraste (bordures, fonds, texte secondaire).
2. Clarifier la séparation :
   - `style.css` = thème global (login + composants communs)
   - `dashboard.css` = thème dashboard (sidebar + surface principale)
   - ajouter un **fichier commun** (ex: `base.css`) pour boutons/form/table/inputs.
3. Corriger `analyses.fxml` (et plus tard les autres) pour que le thème appliqué soit cohérent (dashboard vs global).

**Fichiers concernés**
- `javafx-client/src/main/resources/styles/style.css`
- `javafx-client/src/main/resources/styles/dashboard.css`
- (nouveau) `javafx-client/src/main/resources/styles/base.css` (recommandé)
- `javafx-client/src/main/resources/views/analyses.fxml`

**Livrables attendus**
- Boutons/inputs/table cohérents partout.
- Moins de “sauts” visuels entre pages.

---

### Phase 2 — Standardiser les écrans (P1)
**But :** remplacer les stubs par des écrans “SaaS skeleton” (sans logique métier complexe).

**Actions**
1. Créer un pattern commun “Page” (FXML réutilisable) :
   - top header (titre + sous-titre optionnel)
   - toolbar optionnelle (rechercher, bouton ajouter, rafraîchir)
   - area content (table/cards/forms)
2. Remplacer les `style="-fx-..."` inline dans :
   - `patient.fxml`, `medecin.fxml`, `rendezvous.fxml`, `prescriptions.fxml`, `revenus.fxml`
3. Uniformiser le header pour chaque page :
   - `Label` avec `styleClass="page-title"`
   - `Label` avec `styleClass="page-subtitle"`

**Fichiers concernés**
- `javafx-client/src/main/resources/views/patient.fxml`
- `javafx-client/src/main/resources/views/medecin.fxml`
- `javafx-client/src/main/resources/views/rendezvous.fxml`
- `javafx-client/src/main/resources/views/prescriptions.fxml`
- `javafx-client/src/main/resources/views/revenus.fxml`

**Livrables attendus**
- Tous les écrans ont le même squelette, même padding, même typographie.

---

### Phase 3 — Dashboard SaaS (P2)
**But :** rendre le `main-dashboard.fxml` plus “produit SaaS”.

**Actions**
1. Ajouter une **topbar** dans le `BorderPane` :
   - zone centre au contenu
   - à gauche : breadcrumb ou page actuelle
   - à droite : recherche rapide / notifications / avatar
2. Améliorer la sidebar :
   - passer de `selected` via style au standard “active item” clairement visible
   - ajouter icônes cohérentes (actuellement emoji → ok mais standardiser tailles/alignements)
3. Ajouter des **cartes de stats** réutilisables :
   - utiliser la classe existante `WidgetCard` (Java) si déjà utilisée
   - ou créer des classes FXML CSS pour stats cards.
4. Établir des **states** :
   - empty state
   - loading state (spinner simple)
   - error state (dialog cohérente)

**Fichiers concernés**
- `javafx-client/src/main/resources/views/main-dashboard.fxml`
- `javafx-client/src/main/resources/styles/dashboard.css`
- (si besoin) `javafx-client/src/main/java/com/gindho/client/components/WidgetCard.java`

---

### Phase 4 — Tableaux & formulaires “niveau SaaS” (P2)
**But :** rendre les listes plus lisibles et utilisables.

**Actions**
1. TableView :
   - style plus “soft” : bordures plus légères, en-têtes distincts
   - alignements (left/center) cohérents
   - “actions column” avec boutons stylés (icônes + tooltips)
2. Formulaires :
   - styles d’erreur (`.field-error`, `.error-text`)
   - focus states plus visibles
   - placeholder / prompt uniformes
3. Badges :
   - urgent / status actif / role admin, etc.

**Fichiers concernés**
- `javafx-client/src/main/resources/styles/style.css`
- `javafx-client/src/main/resources/styles/dashboard.css`
- `javafx-client/src/main/resources/views/analyses.fxml` (et futures vues)

---

### Phase 5 — Qualité, tests visuels & cohérence (P3)
**But :** valider que l’UI ne casse pas.

**Actions**
1. Vérifier les écrans aux tailles typiques :
   - 1366×768 (desktop)
   - 375–414 px de large (si la fenêtre peut être redimensionnée)
2. S’assurer que :
   - labels ne débordent pas
   - textes longs wrap correctement
   - boutons gardent une hauteur lisible
3. Clarifier l’usage des stylesheets :
   - une feuille “base”
   - une feuille “dashboard”
   - une feuille “login”
4. Optionnel : documentation interne (comment créer de nouvelles pages avec le skeleton).

---

## Modifs concrètes recommandées (checklist par fichier)

### `login.fxml`
- Remplacer styles inline par `styleClass` quand c’est possible.
- Uniformiser le conteneur de formulaire (radius/border/padding) avec tokens.
- Harmoniser `continueButton` avec les classes CSS primaires existantes (`primary-btn`, `login-continue-btn`).

### `main-dashboard.fxml`
- Ajouter `topbar` + classes CSS (`topbar`, `topbar-title`, `topbar-actions`).
- Corriger les classes manquantes (ex: `user-name`, `user-role`) si elles ne sont pas définies.

### Vues stub
- Retirer `style="-fx-background-color: ..."` et utiliser classes :
  - `page-root`, `page-content`, `page-title`, `page-subtitle`.

### `analyses.fxml`
- S’assurer que la feuille CSS utilisée est celle du dashboard (sinon incohérences).
- Harmoniser le header (retour) et le toolbar (boutons cohérents).

---

## Calendrier suggéré
- **Semaine 1 (P1)** : tokens + harmonisation CSS + correction analyses + skeleton pages pour stubs.
- **Semaine 2 (P2)** : topbar + amélioration dashboard + badges + boutons/table plus “SaaS”.
- **Semaine 3 (P2/P3)** : polish final + états (loading/empty/error) + cohérence globale.

---

## Critères de réussite
- Visuellement : toutes pages partagent le même style (mêmes polices, mêmes surfaces, mêmes contrôles).
- Fonctionnel : navigation, boutons, formulaires restent clairs et actionnables.
- Technique : suppression partielle des styles inline + CSS centralisé.
- Maintenabilité : ajouter une nouvelle vue devient simple (copier le skeleton + utiliser `styleClass`).
