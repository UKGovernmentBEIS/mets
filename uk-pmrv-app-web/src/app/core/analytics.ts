/* eslint-disable */
// @ts-nocheck

let _propertyId: string;

export function initializeGoogleAnalytics(measurementId: string, propertyId: string) {
  _propertyId = propertyId;
  if (!measurementId) {
    console.warn('No google measurementId found. Environment will not log google analytics.');
    return;
  }
  if (!propertyId) {
    console.warn('No google propertyId found. Environment will not log google analytics.');
    return;
  }
  console.log('Logging to Google Analytics with measurementId', measurementId);

  const gtagScript = document.createElement('script');
  gtagScript.async = true;
  gtagScript.src = `https://www.googletagmanager.com/gtag/js?id=${measurementId}`;
  document.head.appendChild(gtagScript);
  (window as any).dataLayer = (window as any).dataLayer || [];
  window.gtag = function () {
    dataLayer.push(arguments);
  };
  gtag('js', new Date());

  gtag('config', measurementId);
  toggleAnalytics(false);
}
export function gtagIsAvailable(): boolean {
  return 'gtag' in window && typeof window.gtag === 'function';
}
export function logGoogleEvent(eventName: string, parameters: Record<string, unknown> = {}) {
  if (!gtagIsAvailable()) {
    return;
  }
  gtag('event', eventName, parameters);
}
export function toggleAnalytics(enabled: boolean) {
  if (!_propertyId) {
    return;
  }
  console.log('analytics enabled', enabled);
  window['disable-ga-' + _propertyId] = enabled;
}
