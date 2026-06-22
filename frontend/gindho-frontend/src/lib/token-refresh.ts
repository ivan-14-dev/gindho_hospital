/**
 * Gestion des refresh tokens pour cookie HttpOnly
 * Compatible avec identity-service et Keycloak
 */

export async function refreshToken(): Promise<string | null> {
  try {
    const response = await fetch('/api/auth/refresh', {
      method: 'POST',
      credentials: 'include',
    });
    
    if (!response.ok) {
      return null;
    }
    
    const data = await response.json();
    return data.token || null;
  } catch {
    return null;
  }
}

export function setupTokenRefreshInterceptors() {
  // Cette fonction sera appelée au démarrage de l'app
  // Pour rafraîchir automatiquement le token expiré
}

export function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
}