// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { KeycloakConfig, KeycloakInitOptions } from 'keycloak-js';

const keycloakConfig: KeycloakConfig = {
  url: '',
  realm: 'uk-pmrv',
  clientId: 'uk-pmrv-web-app',
};

const keycloakInitOptions: KeycloakInitOptions = {
  onLoad: 'check-sso',
  enableLogging: true,
  pkceMethod: 'S256',
};

const apiOptions = {
  baseUrl: '//localhost:8080/api',
};

const timeoutBanner = {
  timeOffsetSeconds: 120,
};

export const environment = {
  production: false,
  keycloakConfig,
  keycloakInitOptions,
  apiOptions,
  timeoutBanner,
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
