import { API_CONFIG } from './config';

type UnauthorizedHandler = () => void;
type ForbiddenHandler = () => void;

let unauthorizedCallback: UnauthorizedHandler | null = null;
let forbiddenCallback: ForbiddenHandler | null = null;

export const setUnauthorizedHandler = (handler: UnauthorizedHandler) => {
  unauthorizedCallback = handler;
};

export const setForbiddenHandler = (handler: ForbiddenHandler) => {
  forbiddenCallback = handler;
};

class ApiClient {
  private baseUrl: string;

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl;
  }

  async request<T>(
    path: string,
    options: RequestInit = {},
    retryCount = 0
  ): Promise<T> {
    const token = localStorage.getItem('token');
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    };

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);

    try {
      const response = await fetch(`${this.baseUrl}${path}`, {
        ...options,
        headers,
        signal: controller.signal,
      });

      clearTimeout(timeoutId);

      if (!response.ok) {
        if (response.status === 401) {
          unauthorizedCallback?.();
          throw new Error('Session expirée - Veuillez vous reconnecter');
        }
        if (response.status === 403) {
          forbiddenCallback?.();
          throw new Error('Accès refusé - Vous n\'avez pas les permissions nécessaires');
        }
        const error = await response.json().catch(() => ({
          message: `HTTP ${response.status}: ${response.statusText}`,
        }));
        throw new Error(error.message || error.error || `HTTP ${response.status}`);
      }

      return response.json();
    } catch (error) {
      clearTimeout(timeoutId);

      // Retry logic for network errors
      if (retryCount < API_CONFIG.RETRY_ATTEMPTS && this.shouldRetry(error)) {
        await this.delay(API_CONFIG.RETRY_DELAY);
        return this.request<T>(path, options, retryCount + 1);
      }

      throw error;
    }
  }

  private shouldRetry(error: unknown): boolean {
    if (error instanceof TypeError) return true; // Network error
    if (error instanceof DOMException && error.name === 'AbortError') return false;
    return false;
  }

  private delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  get<T>(path: string): Promise<T> {
    return this.request<T>(path, { method: 'GET' });
  }

  post<T>(path: string, body: unknown): Promise<T> {
    return this.request<T>(path, {
      method: 'POST',
      body: JSON.stringify(body),
    });
  }

  put<T>(path: string, body: unknown): Promise<T> {
    return this.request<T>(path, {
      method: 'PUT',
      body: JSON.stringify(body),
    });
  }

  patch<T>(path: string, body: unknown): Promise<T> {
    return this.request<T>(path, {
      method: 'PATCH',
      body: JSON.stringify(body),
    });
  }

  delete<T>(path: string): Promise<T> {
    return this.request<T>(path, { method: 'DELETE' });
  }
}

// Instance unique pour toute l'application
export const apiClient = new ApiClient(API_CONFIG.GATEWAY_URL);

// Export de la configuration pour les services
export { API_CONFIG };