import { useState, useEffect } from 'react';

export interface RuntimeConfig {
  gatewayUrl: string;
  keycloakUrl: string;
  keycloakRealm: string;
  keycloakClientId: string;
  appEnv: string;
  appVersion: string;
}

const defaultConfig: RuntimeConfig = {
  gatewayUrl: 'http://localhost:8080',
  keycloakUrl: 'http://localhost:8081',
  keycloakRealm: 'gindho',
  keycloakClientId: 'gindho-frontend',
  appEnv: 'development',
  appVersion: '1.0.0',
};

export function useRuntimeConfig(): RuntimeConfig {
  const [config, setConfig] = useState<RuntimeConfig>(defaultConfig);

  useEffect(() => {
    // Try to fetch runtime config (for Docker deployment)
    fetch('/runtime-config.json')
      .then((res) => res.json())
      .then((data) => {
        // Replace placeholders with env variables or defaults
        const parsed = {
          gatewayUrl: data.gatewayUrl?.replace('__GATEWAY_URL__', import.meta.env.VITE_GATEWAY_URL || defaultConfig.gatewayUrl),
          keycloakUrl: data.keycloakUrl?.replace('__KEYCLOAK_URL__', import.meta.env.VITE_KEYCLOAK_URL || defaultConfig.keycloakUrl),
          keycloakRealm: data.keycloakRealm?.replace('__KEYCLOAK_REALM__', import.meta.env.VITE_KEYCLOAK_REALM || defaultConfig.keycloakRealm),
          keycloakClientId: data.keycloakClientId?.replace('__KEYCLOAK_CLIENT_ID__', import.meta.env.VITE_KEYCLOAK_CLIENT_ID || defaultConfig.keycloakClientId),
          appEnv: data.appEnv?.replace('__APP_ENV__', import.meta.env.VITE_APP_ENV || defaultConfig.appEnv),
          appVersion: data.appVersion?.replace('__APP_VERSION__', import.meta.env.VITE_APP_VERSION || defaultConfig.appVersion),
        };
        setConfig(parsed);
      })
      .catch(() => {
        // Fallback to build-time config
        setConfig({
          ...defaultConfig,
          gatewayUrl: import.meta.env.VITE_GATEWAY_URL || defaultConfig.gatewayUrl,
          keycloakUrl: import.meta.env.VITE_KEYCLOAK_URL || defaultConfig.keycloakUrl,
          keycloakRealm: import.meta.env.VITE_KEYCLOAK_REALM || defaultConfig.keycloakRealm,
          keycloakClientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || defaultConfig.keycloakClientId,
          appEnv: import.meta.env.VITE_APP_ENV || defaultConfig.appEnv,
          appVersion: import.meta.env.VITE_APP_VERSION || defaultConfig.appVersion,
        });
      });
  }, []);

  return config;
}