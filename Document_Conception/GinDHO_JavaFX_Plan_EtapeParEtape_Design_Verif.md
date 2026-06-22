# GinDHO — Plan d’évolution JavaFX (Design + Vérifications)  
*(Architecture : JavaFX Desktop → API Spring Boot. Objectif : cohérence UI “SaaS”, suppression progressive des styles inline, validation design par étapes, et gardes-fous techniques.)*

## 0) Pré-conditions & convention de vérification
### Environnement
- Java 21
- Maven (projet dans `javafx-client/`)
- Backend déjà fonctionnel à `http://localhost:8080`

### Commandes Maven (client JavaFX)
Dans la racine du repo :
- **Compil + tests**
```bash
mvn -f javafx-client/pom.xml clean test
```
- **Compilation rapide (avant run / après modifs)**
```bash
mvn -f javafx-client/pom.xml clean compile
```
- **Lancer l’app JavaFX**
```bash
mvn -f javafx-client/pom.xml javafx:run
```

> Règle stricte : après chaque modification (code Java, CSS, FXML), on exécute **au minimum** `clean compile`, puis on run si OK.

---

## 1) Phase 1 — Verrou technique : `MainDashboardController` doit compiler et rendre le dashboard
### 1.1 Objectif
Garantir que la vue principale (admin/medecin) charge sans erreur, afin de pouvoir valider le design (topbar/sidebar/stats/widgets).

### 1.2 Étapes
1. Exécuter :
   ```bash
   mvn -f javafx-client/pom.xml clean compile
   ```
2. Si erreurs de compilation :
   - relever le fichier + ligne depuis la sortie Maven
   - corriger
   - refaire `clean compile` jusqu’à succès
3. Une fois compilation OK :
   ```bash
   mvn -f javafx-client/pom.xml javafx:run
   ```
4. QA minimal à l’écran :
   - login OK
   - dashboard s’affiche
   - navigation sidebar affiche bien le contenu (navigation ↔ `contentArea` / `roleContentArea`)

### 1.3 Critères de réussite
- `mvn ... clean compile` passe
- Dashboard visible (sans stacktrace console)
- Sidebar navigation fonctionnelle (pas de FXML manquante / pas de null pointer)

---

## 2) Phase 2 — Harmoniser le design (CSS tokens/classes) sans casser (audit → conversion ciblée)
### 2.1 Objectif
Réduire la dérive visuelle en remplaçant les `style="-fx-..."` inline par des `styleClass` cohérentes avec :
- `javafx-client/src/main/resources/styles/base.css`  
  (primitives : `page-root`, `page-content`, `page-title`, etc.)
- `javafx-client/src/main/resources/styles/style.css`  
  (composants : `button`, `primary-btn`, `nav-back-btn`, tables, badges, inputs)
- `javafx-client/src/main/resources/styles/dashboard.css`  
  (dashboard : `sidebar`, `topbar`, `stat-card`, user labels, etc.)

### 2.2 Étapes de design (audit objectif)
#### 2.2.1 Lister les styles inline restant
Dans le repo :
```bash
grep -R --line-number 'style="-' javafx-client/src/main/resources/views
```

#### 2.2.2 Vérifier que les `styleClass` utilisées existent dans les CSS
Principe : chaque `styleClass="X"` présent dans les FXML doit correspondre à une règle CSS (ou à un style JavaFX standard).  
Contrôle manuel recommandé :
- Ouvrir `base.css`, `style.css`, `dashboard.css` et vérifier les classes utilisées par les vues “déjà propres”.

### 2.3 Conversion ciblée (priorité P1) : `login.fxml`
Constat : `login.fxml` contient encore des styles inline sur plusieurs Labels/texte/bouton.

**Étapes :**
1. Extraire la liste des inline de `login.fxml` :
   ```bash
   grep -n 'style="-' javafx-client/src/main/resources/views/login.fxml
   ```
2. Pour chaque inline :
   - créer une classe CSS dédiée dans `style.css` (ou `base.css` si c’est un primitive global)
   - remplacer `style="..."` par `styleClass="..."`
3. Vérifier :
   - `mvn -f javafx-client/pom.xml clean compile`
   - `mvn -f javafx-client/pom.xml javafx:run`
   - QA visuel : titres, labels, bouton “Se connecter”, spacing/tailles identiques

### 2.4 Critères de réussite (design P1)
- `patient.fxml` + `analyses.fxml` gardent le même rendu
- Login utilise principalement des `styleClass` (quasi zéro inline)
- Focus clavier reste visible (règles `:focused` déjà prévues dans les CSS)

---

## 3) Phase 3 — Standardiser les écrans “skeleton SaaS” (pattern FXML + suppression inline)
*(Objectif : même structure → même padding → même hiérarchie → mêmes surfaces.)*

### 3.1 Étapes (par vue)
Vérifier/standardiser :
- `patient.fxml` ✅
- `analyses.fxml` ✅
- `main-dashboard.fxml` ✅ (topbar + sidebar + cartes)

À harmoniser ensuite (probable) :
- `medecin.fxml`
- `rendezvous.fxml`
- `prescriptions.fxml`
- `revenus.fxml`
- `upcoming-appointments.fxml`

Pour chaque vue :
1. Vérifier que le layout respecte le pattern :
   - racine `VBox.page-root` / `BorderPane` pour dashboard
   - `VBox.page-content`
   - header row : `page-title` + bouton retour `nav-back-btn` (quand applicable)
   - toolbar : `section-toolbar`
   - contenu : `table-card` + `table-view` (ou surface card dédiée)
2. Remplacer les `style="...` inline par des `styleClass`
3. QA contrôles :
   - pas de texte coupé dans les boutons
   - TableView : en-têtes lisibles, scroll fonctionnel
   - pas de débordement horizontal lors du resize

### 3.2 Commandes de vérification design
1. Compile :
   ```bash
   mvn -f javafx-client/pom.xml clean compile
   ```
2. Run :
   ```bash
   mvn -f javafx-client/pom.xml javafx:run
   ```
3. QA manuel :
   - réduire légèrement la largeur fenêtre
   - vérifier tables + champs
   - tester focus clavier (Tab sur inputs/buttons)

### 3.3 Critères de réussite (skeleton)
- Les écrans partagent les mêmes classes de base (`page-title`, `section-toolbar`, `table-card`, `table-view`)
- L’interface “ne saute pas” visuellement d’un écran à l’autre

---

## 4) Phase 4 — Dashboard SaaS : états + widgets (P2)
### 4.1 Objectif
Valider que le dashboard a :
- des stats cards cohérentes
- des sections role-based
- des états (empty/error) fiables
- un rendu stable (pas d’alignement casse sur resize)

### 4.2 Étapes : valider le style des Stats Cards
Dans `MainDashboardController`, les cartes utilisent :
- `stat-card`, `stat-icon`, `stat-value`, `stat-label`
- `mini-bars`, `mini-bar`

**Actions :**
1. Vérifier visuellement :
   - `statsGrid` se place en grille propre
   - les valeurs sont lisibles
2. Contrôle “robustesse” :
   - si la valeur est vide/non numérique, le rendu ne doit pas casser

### 4.3 Étapes : ajouter/valider les états (empty/error/loading)
Dans le dashboard, on observe des placeholders :
- `premium-empty-label`
- `premium-placeholder-label`

**Étapes :**
1. Simuler “liste vide” (backend renvoie une liste vide ou désactiver une charge)
2. Vérifier :
   - empty state affiché à la place du tableau
   - pas de `null` dans les labels
3. Simuler “erreur backend” :
   - vérifier que le message est compréhensible (pas d’UI vide silencieuse)

### 4.4 Critères de réussite
- Aucune section n’est “blanche” sans explication
- Les labels placeholders utilisent un style consistent (`premium-*`)

---

## 5) Phase 5 — QA Design “niveau produit” (P3)
### 5.1 Checklist de QA (2 tailles)
1. Desktop : ~1366×768
2. Petite fenêtre : largeur réduite (ex: 1000px ou moins si redimensionnable)

Checklist :
- aucun texte coupé (boutons, en-têtes TableView, labels)
- boutons cliquables (hauteur suffisante)
- TableView : en-têtes visibles, scroll OK
- focus clavier : visible sur champs/boutons
- pas d’incohérence de thème (ex: `analyses.fxml` n’hérite pas d’un autre thème cassé)

### 5.2 Anti-régression CSS
Après un lot de modifications CSS :
- exécuter :
  ```bash
  mvn -f javafx-client/pom.xml clean compile
  mvn -f javafx-client/pom.xml javafx:run
  ```
- vérifier au moins :
  - login
  - patient
  - analyses
  - dashboard

---

## 6) Deliverables attendus (résumé)
- [ ] `MainDashboardController` compile et dashboard charge (Phase 1)
- [ ] Réduction des styles inline dans les FXML (Phase 2→3)
- [ ] Les vues partagent un skeleton SaaS cohérent (Phase 3)
- [ ] Dashboard : stats + sections role-based + states (Phase 4)
- [ ] QA design sur 2 tailles (Phase 5)
