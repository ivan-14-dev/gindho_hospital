import * as Sentry from '@sentry/react';

export function initSentry() {
  if (import.meta.env.VITE_APP_ENV === 'production') {
    Sentry.init({
      dsn: import.meta.env.VITE_SENTRY_DSN,
      environment: import.meta.env.VITE_APP_ENV,
      release: `gindho-frontend@${import.meta.env.VITE_APP_VERSION}`,
      tracesSampleRate: 0.1,
      profilesSampleRate: 0.1,
      integrations: [
        Sentry.browserTracingIntegration(),
        Sentry.replayIntegration({
          maskAllText: true,
          blockAllMedia: true,
        }),
      ],
    });
  }
}

export { Sentry };