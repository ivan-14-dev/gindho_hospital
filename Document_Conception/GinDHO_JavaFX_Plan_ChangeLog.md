# GinDHO — ChangeLog du plan JavaFX (Design + Vérifications)

## Contexte
Le fichier source de planification était :
- `Document_Conception/GinDHO_JavaFX_Plan_EtapeParEtape_Design_Verif.md`

L’objectif demandé était :
- d’utiliser ce fichier existant,
- de l’améliorer,
- et de **documenter dans un autre document** ce qui a été fait.

---

## Modifications appliquées au plan (contenu final)
### 1) Clarification et renforcement de la “convention de vérification”
**Ce qui a changé**
- Ajout/renforcement d’une règle explicite : après chaque modification (code/CSS/FXML) → exécuter **au minimum** `mvn -f javafx-client/pom.xml clean compile`, puis `javafx:run` si OK.

**Pourquoi**
- Réduire le risque de divergences UI non validées et rendre le plan “exécutable”.

---

### 2) Phase 1 : “verrou technique” mieux cadré pour valider le design dashboard
**Ce qui a changé**
- La Phase 1 indique clairement :
  - compilation via `clean compile`
  - puis lancement via `javafx:run`
  - et un QA minimal : login OK, dashboard visible, navigation sidebar ↔ `contentArea` / `roleContentArea`.

**Pourquoi**
- Garantir que les tests visuels peuvent démarrer (puisque `MainDashboardController` est critique côté compilation/affichage).

---

### 3) Phase 2 : ajout d’un audit objectif (styles inline) avant conversion
**Ce qui a changé**
- Ajout d’étapes d’extraction :
  - `grep -R --line-number 'style="-' javafx-client/src/main/resources/views`
- Ajout d’un principe de contrôle :
  - chaque `styleClass="X"` doit correspondre à une règle CSS (base/style/dashboard) ou à un style JavaFX standard.

**Pourquoi**
- Le plan ne se contente plus de “recommandations”, il donne une procédure mesurable.

---

### 4) Phase 2 : conversion ciblée et priorisée sur `login.fxml`
**Ce qui a changé**
- Focus explicite sur `login.fxml` :
  - extraire les inline : `grep -n 'style="-' .../login.fxml`
  - créer des classes CSS dédiées
  - remplacer `style="..."` par `styleClass="..."`

**Pourquoi**
- Réduire rapidement la dérive visuelle et améliorer la cohérence avec `style.css` / `base.css`.

---

### 5) Phase 3 : standardisation skeleton SaaS avec critères de QA
**Ce qui a changé**
- La Phase 3 liste :
  - ce qui est déjà OK (`patient.fxml`, `analyses.fxml`, `main-dashboard.fxml`)
  - ce qui doit être harmonisé ensuite (medecin/rendezvous/prescriptions/revenus/upcoming-appointments)
- Ajoute des critères QA concrets :
  - pas de clipping
  - header/toolbar/padding cohérents
  - Tables + champs OK
  - focus clavier via Tab.

**Pourquoi**
- Transformer “standardiser les écrans” en actions vérifiables.

---

### 6) Phase 4 : dashboard états (empty/error/loading) cadrés
**Ce qui a changé**
- Ajout d’un plan pour vérifier :
  - placeholders `premium-empty-label` / `premium-placeholder-label`
  - robustesse quand les données sont vides
  - lisibilité des erreurs backend

**Pourquoi**
- Éviter une UI “silencieuse” en cas d’échec ou données manquantes.

---

### 7) Phase 5 : QA design sur 2 tailles + anti-régression
**Ce qui a changé**
- Ajout d’une checklist sur :
  - ~1366×768
  - petite largeur (selon redimensionnement)
- Ajout d’un “anti-régression” : relancer compile + run après changements CSS.

**Pourquoi**
- Empêcher les régressions lors des itérations CSS.

---

## État d’avancement
- [x] Le fichier de plan a été amélioré et mis à jour (clarifications, audit inline via grep, QA critères).
- [x] Un document séparé (this file) a été créé pour tracer les modifications.

## Fichiers concernés
- Plan (modifié) : `Document_Conception/GinDHO_JavaFX_Plan_EtapeParEtape_Design_Verif.md`
- Changelog (créé) : `Document_Conception/GinDHO_JavaFX_Plan_ChangeLog.md`
