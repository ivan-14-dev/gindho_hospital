# Module Analytics Complet - GinDHO Hospital

## Vue d'ensemble

Le module Analytics offre une suite complète de dashboards et rapports pour l'analyse hospitalière. Il est organisé de manière modulaire avec une navigation latérale et 16 sections principales.

## Architecture

### Structure des Fichiers

```
src/pages/analytics/
├── Analytics.tsx                    # Hub principal (orchestrateur)
├── AnalyticsLayout.tsx             # Layout avec sidebar de navigation
├── AnalyticsChart.tsx              # Composant graphiques (legacy)
├── components/
│   ├── KPICard.tsx                 # Carte d'indicateur clé
│   ├── FilterBar.tsx               # Barre de filtres globaux
│   └── ChartCard.tsx               # Conteneur pour graphiques
└── views/
    ├── ExecutiveDashboard.tsx      # Vue exécutive (12 KPIs)
    ├── ActivityView.tsx             # Activité hospitalière
    ├── FinanceView.tsx              # Analyse financière
    ├── PharmacyView.tsx             # Gestion pharmacie
    ├── LaboratoryView.tsx           # Analyses laboratoire
    ├── PatientsView.tsx             # Données patients
    ├── ConsultationsView.tsx        # Consultations (TODO)
    ├── HospitalizationView.tsx      # Hospitalisations (TODO)
    ├── SurgeryView.tsx              # Bloc opératoire (TODO)
    ├── ImagingView.tsx              # Radiologie (TODO)
    ├── BloodBankView.tsx            # Banque de sang (TODO)
    ├── HRView.tsx                   # Ressources humaines (TODO)
    ├── QualityView.tsx              # Qualité des soins (TODO)
    ├── EpidemiologyView.tsx         # Épidémiologie (TODO)
    ├── SatisfactionView.tsx         # Satisfaction patients (TODO)
    └── ReportsView.tsx              # Rapports (TODO)
```

## Composants Principaux

### 1. KPICard
Affiche un indicateur clé avec:
- Icône colorée
- Valeur principale
- Pourcentage de variation
- Tendance (up/down/flat)
- Subtitle optionnel

```typescript
<KPICard
  title="Total Patients"
  value={1234}
  change={12.5}
  trend="up"
  icon={Users}
  color="text-blue-600"
/>
```

### 2. FilterBar
Barre de filtres globaux avec:
- Sélection de période (jour, semaine, mois, année, personnalisé)
- Plage de dates (mode personnalisé)
- Filtres avancés (service, spécialité, médecin, sexe)
- Bouton de réinitialisation
- Bouton d'export

```typescript
<FilterBar
  dateRange={dateRange}
  onDateRangeChange={setDateRange}
  period={period}
  onPeriodChange={setPeriod}
  filters={filters}
  onFilterChange={onFilterChange}
  onClearFilters={onClearFilters}
  onExport={handleExport}
/>
```

### 3. ChartCard
Conteneur pour graphiques avec:
- Titre avec icône
- Boutons de rafraîchissement et d'export
- État de chargement
- Subtitle optionnel

```typescript
<ChartCard
  title="Évolution des Patients"
  icon={TrendingUp}
  subtitle="Données mensuelles"
  isLoading={isLoading}
  onRefresh={handleRefresh}
  onExport={handleExport}
>
  {/* Contenu du graphique */}
</ChartCard>
```

### 4. AnalyticsLayout
Layout avec sidebar collapsible contenant:
- Navigation vers 16 sections principales
- Icônes colorées pour chaque section
- Mode responsive (sidebar rétractable)
- Contenu principal scrollable

## Sections Disponibles

### Implémentées ✅

1. **Vue Exécutive** - 12 KPIs principaux
   - Total patients, consultations, admissions, sorties
   - Hospitalisés, lits disponibles, taux occupation
   - Temps d'attente, revenus, dépenses
   - Chirurgies, satisfaction patients

2. **Activité Hospitalière** - 6 graphiques
   - Consultations par jour, semaine, mois, année
   - Répartition par spécialité et médecin
   - Répartition par âge et sexe

3. **Finances** - 4 graphiques
   - Revenus quotidiens/mensuels/annuels
   - Factures payées vs impayées
   - Recettes par service
   - Dépenses et marges

4. **Pharmacie** - 4 graphiques
   - Top 10 médicaments prescrits
   - Stock critique
   - Valeur et rotation du stock
   - Coût des médicaments

5. **Laboratoire** - 4 graphiques
   - Nombre d'analyses
   - Types d'examens
   - Temps moyen de traitement
   - Résultats validés

### À Implémenter (Stubs) 🔄

- Patients
- Consultations
- Hospitalisations
- Bloc Opératoire
- Radiologie
- Banque de Sang
- Ressources Humaines
- Qualité des Soins
- Épidémiologie
- Satisfaction
- Rapports

## Filtres Globaux

Tous les graphiques peuvent être filtrés par:

- **Période**: Jour, Semaine, Mois, Année, Personnalisé
- **Service**: Urgences, Chirurgie, Maternité, Pédiatrie, Cardiologie
- **Spécialité**: Médecine générale, Cardiologie, Neurologie, Orthopédie, etc.
- **Médecin**: Recherche par nom
- **Sexe**: Homme, Femme, Autre
- **Région**: Villes/régions (TODO)
- **Type Patient**: Ambulatoire, Hospitalisé, Urgence (TODO)
- **Assurance**: Par type d'assurance (TODO)

## Types de Graphiques Recommandés

### Implémentés ✅
- KPI Cards (12 par dashboard)
- Bar charts (comparaisons)
- Line charts (évolutions)

### À Ajouter 📊
- Pie charts (répartitions)
- Area charts (tendances)
- Heatmaps (occupation lits)
- Treemaps (coûts/dépenses)
- Maps (géolocalisation patients)
- Gauges (pourcentages)
- Calendars (activité quotidienne)

## État des Graphiques

Actuellement, les emplacements des graphiques sont des stubs affichant:
```
<div className="h-64 flex items-center justify-center text-muted-foreground">
  <p>Graphique - Intégration Recharts</p>
</div>
```

### Pour Intégrer Recharts:

1. Installer: `pnpm add recharts`
2. Importer: `import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'`
3. Remplacer les stubs par les composants Recharts

## Fonctionnalités Avancées (TODO)

- [ ] Création de dashboards personnalisés par utilisateur
- [ ] Export des graphiques (PDF, Excel, PNG)
- [ ] Envoi automatique de rapports par email
- [ ] Comparaison de deux périodes
- [ ] Prédictions basées sur l'historique
- [ ] Détection automatique d'anomalies
- [ ] Alertes configurables
- [ ] Sauvegarde des filtres favoris
- [ ] Drill-down sur les graphiques
- [ ] Annotation sur graphiques

## API Endpoints

Le module utilise les endpoints suivants (à implémenter):

```typescript
// Vue Exécutive
GET /analytics-service/executive-metrics

// Activité
GET /analytics-service/activity

// Consultations
GET /analytics-service/consultations

// Hospitalisations
GET /analytics-service/hospitalizations

// Finances
GET /analytics-service/finance

// Pharmacie
GET /analytics-service/pharmacy

// Laboratoire
GET /analytics-service/laboratory

// Et autres sections...
```

## Utilisation

### Naviguer entre les sections:

1. Cliquer sur un élément de la sidebar
2. La section correspondante s'affiche
3. Les filtres s'appliquent automatiquement

### Appliquer des filtres:

1. Cliquer sur "Filtres" en haut
2. Sélectionner période/service/etc.
3. Les graphiques se mettent à jour en temps réel

### Exporter:

1. Cliquer sur l'icône de téléchargement sur chaque graphique
2. Choisir le format (PDF/Excel/CSV)

## Performance

- React Query pour la mise en cache automatique
- Pagination pour les grandes données
- Lazy loading des graphiques
- Debounce sur les filtres (TODO)

## Accessibilité

- ✅ Icônes avec labels
- ✅ Contraste des couleurs
- ✅ Navigation au clavier
- ⚠️ ARIA labels (à améliorer)

## Prochaines Étapes

1. Implémenter les 11 sections restantes (views)
2. Intégrer Recharts pour les graphiques
3. Ajouter les API endpoints backend
4. Implémenter les filtres avancés
5. Ajouter les fonctionnalités d'export
6. Tests E2E pour chaque section

## Notes de Développement

- Tous les composants utilisent shadcn/ui
- Icônes de Lucide React
- TypeScript 100% (pas de `any`)
- Responsive design (mobile/tablet/desktop)
- Tailwind CSS pour le styling

---

Pour plus d'informations, consultez la documentation complète du frontend.
