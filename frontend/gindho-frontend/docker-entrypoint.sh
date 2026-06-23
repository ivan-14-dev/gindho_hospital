#!/bin/sh
# Docker entrypoint script for runtime environment configuration

# Generate runtime config if not exists
if [ ! -f /usr/share/nginx/html/runtime-config.json ]; then
    cat > /usr/share/nginx/html/runtime-config.json << EOF
{
  "gatewayUrl": "${VITE_GATEWAY_URL:-http://localhost:9001}",
  "keycloakUrl": "${VITE_KEYCLOAK_URL:-http://localhost:9004}",
  "keycloakRealm": "${VITE_KEYCLOAK_REALM:-gindho}",
  "keycloakClientId": "${VITE_KEYCLOAK_CLIENT_ID:-gindho-frontend}",
  "appEnv": "${VITE_APP_ENV:-production}",
  "appVersion": "${VITE_APP_VERSION:-1.0.0}"
}
EOF
fi

# Replace environment variables in HTML if needed
if [ -n "$GATEWAY_URL" ]; then
    sed -i "s|http://localhost:9001|${GATEWAY_URL}|g" /usr/share/nginx/html/index.html 2>/dev/null || true
fi

exec "$@"