# Guide d'Intégration Recharts - Module Analytics

## Installation

```bash
pnpm add recharts
```

## Composants Recharts Utilisés

### 1. LineChart (Graphiques Linéaires)
Pour afficher les évolutions dans le temps.

```typescript
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const data = [
  { month: 'Jan', patients: 120, consultations: 80 },
  { month: 'Feb', patients: 150, consultations: 95 },
  { month: 'Mar', patients: 180, consultations: 110 },
];

<ResponsiveContainer width="100%" height={300}>
  <LineChart data={data}>
    <CartesianGrid strokeDasharray="3 3" />
    <XAxis dataKey="month" />
    <YAxis />
    <Tooltip />
    <Legend />
    <Line type="monotone" dataKey="patients" stroke="#3b82f6" />
    <Line type="monotone" dataKey="consultations" stroke="#f97316" />
  </LineChart>
</ResponsiveContainer>
```

### 2. BarChart (Graphiques en Barres)
Pour les comparaisons entre catégories.

```typescript
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const data = [
  { service: 'Urgences', patients: 400, capacity: 800 },
  { service: 'Chirurgie', patients: 300, capacity: 600 },
  { service: 'Maternité', patients: 200, capacity: 400 },
];

<ResponsiveContainer width="100%" height={300}>
  <BarChart data={data}>
    <CartesianGrid strokeDasharray="3 3" />
    <XAxis dataKey="service" />
    <YAxis />
    <Tooltip />
    <Legend />
    <Bar dataKey="patients" fill="#3b82f6" />
    <Bar dataKey="capacity" fill="#e5e7eb" />
  </BarChart>
</ResponsiveContainer>
```

### 3. PieChart (Graphiques Secteurs)
Pour les répartitions en pourcentages.

```typescript
import { PieChart, Pie, Cell, Legend, Tooltip, ResponsiveContainer } from 'recharts';

const data = [
  { name: 'Urgences', value: 400 },
  { name: 'Chirurgie', value: 300 },
  { name: 'Maternité', value: 200 },
];

const COLORS = ['#3b82f6', '#f97316', '#10b981'];

<ResponsiveContainer width="100%" height={300}>
  <PieChart>
    <Pie
      data={data}
      cx="50%"
      cy="50%"
      labelLine={false}
      label={({ name, value }) => `${name}: ${value}`}
      outerRadius={100}
      fill="#8884d8"
      dataKey="value"
    >
      {data.map((entry, index) => (
        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
      ))}
    </Pie>
    <Tooltip />
    <Legend />
  </PieChart>
</ResponsiveContainer>
```

### 4. AreaChart (Graphiques Surfaciques)
Pour visualiser les tendances avec accumulation.

```typescript
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const data = [
  { month: 'Jan', revenue: 10000, expenses: 7000 },
  { month: 'Feb', revenue: 12000, expenses: 8000 },
  { month: 'Mar', revenue: 15000, expenses: 9000 },
];

<ResponsiveContainer width="100%" height={300}>
  <AreaChart data={data}>
    <CartesianGrid strokeDasharray="3 3" />
    <XAxis dataKey="month" />
    <YAxis />
    <Tooltip />
    <Legend />
    <Area type="monotone" dataKey="revenue" stackId="1" stroke="#10b981" fill="#d1fae5" />
    <Area type="monotone" dataKey="expenses" stackId="1" stroke="#ef4444" fill="#fee2e2" />
  </AreaChart>
</ResponsiveContainer>
```

## Exemples par Section

### Vue Exécutive - Évolution des Patients

```typescript
export function PatientsEvolutionChart() {
  const data = [
    { month: 'Jan', patients: 1200, goal: 1000 },
    { month: 'Feb', patients: 1350, goal: 1100 },
    { month: 'Mar', patients: 1480, goal: 1200 },
    { month: 'Apr', patients: 1600, goal: 1300 },
    { month: 'May', patients: 1720, goal: 1400 },
    { month: 'Jun', patients: 1850, goal: 1500 },
  ];

  return (
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="month" />
        <YAxis />
        <Tooltip />
        <Legend />
        <Line type="monotone" dataKey="patients" stroke="#3b82f6" strokeWidth={2} dot={{ r: 4 }} />
        <Line type="monotone" dataKey="goal" stroke="#a1a1a1" strokeDasharray="5 5" />
      </LineChart>
    </ResponsiveContainer>
  );
}
```

### Finances - Revenus vs Dépenses

```typescript
export function FinanceChart() {
  const data = [
    { month: 'Jan', revenue: 150000, expenses: 100000 },
    { month: 'Feb', revenue: 165000, expenses: 105000 },
    { month: 'Mar', revenue: 180000, expenses: 110000 },
    { month: 'Apr', revenue: 195000, expenses: 112000 },
    { month: 'May', revenue: 210000, expenses: 115000 },
    { month: 'Jun', revenue: 230000, expenses: 118000 },
  ];

  return (
    <ResponsiveContainer width="100%" height={300}>
      <AreaChart data={data}>
        <defs>
          <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
            <stop offset="5%" stopColor="#10b981" stopOpacity={0.8} />
            <stop offset="95%" stopColor="#10b981" stopOpacity={0} />
          </linearGradient>
          <linearGradient id="colorExpenses" x1="0" y1="0" x2="0" y2="1">
            <stop offset="5%" stopColor="#ef4444" stopOpacity={0.8} />
            <stop offset="95%" stopColor="#ef4444" stopOpacity={0} />
          </linearGradient>
        </defs>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="month" />
        <YAxis />
        <Tooltip formatter={(value) => `€${value.toLocaleString()}`} />
        <Legend />
        <Area type="monotone" dataKey="revenue" stroke="#10b981" fill="url(#colorRevenue)" />
        <Area type="monotone" dataKey="expenses" stroke="#ef4444" fill="url(#colorExpenses)" />
      </AreaChart>
    </ResponsiveContainer>
  );
}
```

### Activité - Répartition par Spécialité

```typescript
export function SpecialtyDistributionChart() {
  const data = [
    { name: 'Cardiology', value: 25, patients: 450 },
    { name: 'General', value: 20, patients: 360 },
    { name: 'Pediatrics', value: 18, patients: 325 },
    { name: 'Orthopedics', value: 15, patients: 270 },
    { name: 'Neurology', value: 22, patients: 395 },
  ];

  const COLORS = ['#3b82f6', '#f97316', '#10b981', '#8b5cf6', '#ec4899'];

  return (
    <ResponsiveContainer width="100%" height={300}>
      <PieChart>
        <Pie
          data={data}
          cx="50%"
          cy="50%"
          labelLine={false}
          label={({ name, value }) => `${name}: ${value}%`}
          outerRadius={100}
          fill="#8884d8"
          dataKey="value"
        >
          {data.map((entry, index) => (
            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip formatter={(value) => `${value}%`} />
        <Legend />
      </PieChart>
    </ResponsiveContainer>
  );
}
```

### Pharmacie - Top Médicaments

```typescript
export function TopMedicinesChart() {
  const data = [
    { name: 'Paracétamol', prescriptions: 450 },
    { name: 'Ibuprofène', prescriptions: 380 },
    { name: 'Amoxicilline', prescriptions: 320 },
    { name: 'Aspirin', prescriptions: 290 },
    { name: 'Doloprésine', prescriptions: 250 },
    { name: 'Morphine', prescriptions: 180 },
    { name: 'Penicillin', prescriptions: 160 },
    { name: 'Warfarin', prescriptions: 140 },
    { name: 'Metformin', prescriptions: 130 },
    { name: 'Lisinopril', prescriptions: 110 },
  ];

  return (
    <ResponsiveContainer width="100%" height={400}>
      <BarChart data={data} layout="vertical">
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis type="number" />
        <YAxis dataKey="name" type="category" width={100} />
        <Tooltip />
        <Bar dataKey="prescriptions" fill="#06b6d4" />
      </BarChart>
    </ResponsiveContainer>
  );
}
```

## Styles et Personnalisation

### Couleurs Recommandées

```typescript
const COLORS = {
  primary: '#3b82f6',      // Bleu
  success: '#10b981',       // Vert
  danger: '#ef4444',        // Rouge
  warning: '#f97316',       // Orange
  info: '#06b6d4',          // Cyan
  secondary: '#8b5cf6',     // Violet
  muted: '#9ca3af',         // Gris
};
```

### Configuration Recharts

```typescript
const chartConfig = {
  margin: { top: 5, right: 30, left: 0, bottom: 5 },
  style: {
    fontFamily: 'inherit',
  },
  tooltip: {
    contentStyle: {
      backgroundColor: '#fff',
      border: '1px solid #e5e7eb',
      borderRadius: '0.5rem',
    },
  },
};
```

## Prochaines Étapes

1. Créer des composants réutilisables pour chaque type de graphique
2. Intégrer les données API
3. Ajouter des animations
4. Implémenter les interactions (drill-down)
5. Ajouter les exports (PNG, PDF)

---

Pour des exemples plus complets, voir la documentation officielle: https://recharts.org
