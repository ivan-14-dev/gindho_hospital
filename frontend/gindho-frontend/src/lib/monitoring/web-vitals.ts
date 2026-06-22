export function initWebVitals() {
  if (typeof window === 'undefined') return;

  // LCP (Largest Contentful Paint)
  new PerformanceObserver((list) => {
    for (const entry of list.getEntries()) {
      console.log('LCP:', entry);
    }
  }).observe({ type: 'largest-contentful-paint', buffered: true });

  // FID (First Input Delay)
  new PerformanceObserver((list) => {
    for (const entry of list.getEntries()) {
      console.log('FID:', entry);
    }
  }).observe({ type: 'first-input', buffered: true });

  // CLS (Cumulative Layout Shift)
  new PerformanceObserver((list) => {
    for (const entry of list.getEntries()) {
      console.log('CLS:', entry);
    }
  }).observe({ type: 'layout-shift', buffered: true });

  // FCP (First Contentful Paint)
  new PerformanceObserver((list) => {
    for (const entry of list.getEntries()) {
      console.log('FCP:', entry);
    }
  }).observe({ type: 'paint', buffered: true });

  // TTFB (Time to First Byte)
  window.addEventListener('load', () => {
    const navigationEntry = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
    if (navigationEntry) {
      console.log('TTFB:', navigationEntry.responseStart - navigationEntry.requestStart);
    }
  });
}

export function sendToAnalytics(metric: any) {
  // Envoyer à Google Analytics ou autre service
  if (typeof window !== 'undefined' && (window as any).gtag) {
    (window as any).gtag('event', metric.name, {
      value: Math.round(metric.value),
      event_category: 'Web Vitals',
      event_label: metric.id,
      non_interaction: true,
    });
  }
}