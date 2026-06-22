# 005 - Progressive Web App

## Statut
Accepté

## Contexte
Le frontend doit être accessible hors ligne pour les utilisateurs hospitaliers mobiles.

## Décision
- Manifest.json avec icônes multiples
- Service Worker via Workbox
- Cache stratégies : stale-while-revalidate pour APIs, cache-first pour assets
- Queue offline avec IndexedDB

## Conséquences
- Installation possible sur mobile/desktop
- Fonctionnement offline basique
- Expérience native-like