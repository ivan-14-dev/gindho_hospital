# Guide de Déploiement - GinDHO Frontend

## 🚀 Déploiement Production

### Prérequis

- Node.js >= 18.x
- npm >= 9.x
- Accès au cluster Kubernetes
- Image registry (Docker Hub, ECR, GCR, etc.)

### Variables d'environnement

Copier `.env.example` vers `.env.production` et configurer :

```env
# API Configuration
VITE_API_URL=https://api.gindho.com
VITE_KEYCLOAK_URL=https://keycloak.gindho.com
VITE_KEYCLOAK_REALM=gindho
VITE_KEYCLOAK_CLIENT_ID=gindho-frontend

# Monitoring
VITE_SENTRY_DSN=https://xxx@sentry.io/xxx
VITE_ENV=production

# Features
VITE_ENABLE_ANALYTICS=true
VITE_ENABLE_MONITORING=true
```

### Build Production

```bash
# Installation des dépendances
npm ci --only=production

# Build
npm run build

# Preview du build
npm run preview
```

### Docker

```dockerfile
# Dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Kubernetes Deployment

```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: gindho-frontend
  namespace: gindho-production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: gindho-frontend
  template:
    metadata:
      labels:
        app: gindho-frontend
    spec:
      containers:
      - name: gindho-frontend
        image: gindho/frontend:latest
        ports:
        - containerPort: 80
        resources:
          requests:
            cpu: 100m
            memory: 128Mi
          limits:
            cpu: 500m
            memory: 256Mi
        livenessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 10
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: gindho-frontend
  namespace: gindho-production
spec:
  selector:
    app: gindho-frontend
  ports:
  - port: 80
    targetPort: 80
  type: ClusterIP
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: gindho-frontend
  namespace: gindho-production
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  tls:
  - hosts:
    - app.gindho.com
    secretName: gindho-frontend-tls
  rules:
  - host: app.gindho.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: gindho-frontend
            port:
              number: 80
```

### Nginx Configuration

```nginx
# nginx.conf
server {
    listen 80;
    server_name app.gindho.com;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/rss+xml text/javascript;
    gzip_min_length 1000;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' data:; connect-src 'self' https://api.gindho.com wss://api.gindho.com;" always;

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # SPA fallback
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Health check
    location /health {
        access_log off;
        return 200 "healthy\n";
    }
}
```

### CI/CD Pipeline

Le pipeline GitHub Actions est déjà configuré dans `.github/workflows/ci.yml`.

**Étapes automatiques :**
1. Lint & TypeScript check
2. Tests unitaires avec coverage
3. Build de production
4. Scan de sécurité (npm audit, Trivy)
5. Push vers registry
6. Deploy sur Kubernetes (ArgoCD)

### Monitoring Production

#### Sentry
- Erreurs frontend trackées automatiquement
- Performance monitoring activé
- Alertes configurées sur erreurs critiques

#### Web Vitals
- LCP, FID, CLS, FCP, TTFB monitorés
- Dashboard Grafana disponible
- Alertes si dégradation > 20%

### Rollback

```bash
# Rollback Kubernetes
kubectl rollout undo deployment/gindho-frontend -n gindho-production

# Rollback ArgoCD
argocd app rollback gindho-frontend
```

### Health Checks

```bash
# Vérifier le déploiement
kubectl get pods -n gindho-production -l app=gindho-frontend

# Vérifier les logs
kubectl logs -f deployment/gindho-frontend -n gindho-production

# Health check endpoint
curl https://app.gindho.com/health
```

### Performance

- **Bundle initial** : ~265 KB (82 KB gzipped)
- **Chunks lazy-loaded** : 1-40 KB chacun
- **First Load** : < 2s sur 3G
- **Lighthouse Score** : > 90/100

### Sécurité

- ✅ HTTPS obligatoire
- ✅ CSP headers configurés
- ✅ XSS protection
- ✅ CSRF tokens
- ✅ JWT avec refresh automatique
- ✅ RBAC respecté
- ✅ Audit dépendances npm
- ✅ Scan images Docker (Trivy)

### Support

- Documentation: https://docs.gindho.com
- Issues: https://github.com/ivan-14-dev/GinDHO/issues
- Email: support@gindho.com