import { APP_BASE_HREF, PlatformLocation } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ApplicationRef, DoBootstrap, ErrorHandler, NgModule } from '@angular/core';
import { BrowserModule, Title } from '@angular/platform-browser';

import { combineLatest, firstValueFrom } from 'rxjs';

import { initializeGoogleAnalytics } from '@core/analytics';
import { ConfigService } from '@core/config/config.service';
import { AnalyticsInterceptor } from '@core/interceptors/analytics.interceptor';
import { AuthService } from '@core/services/auth.service';
import { LatestTermsService } from '@core/services/latest-terms.service';
import { KeycloakAngularModule, KeycloakOptions, KeycloakService } from 'keycloak-angular';
import { KeycloakConfig } from 'keycloak-js';
import { MarkdownModule } from 'ngx-markdown';

import { ApiModule, Configuration } from 'pmrv-api';

import { environment } from '../environments/environment';
import { AccessibilityComponent } from './accessibility/accessibility.component';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ContactUsComponent } from './contact-us/contact-us.component';
import { CookiesContainerComponent } from './cookies/cookies-container.component';
import { HttpErrorInterceptor } from './core/interceptors/http-error.interceptor';
import { PendingRequestInterceptor } from './core/interceptors/pending-request.interceptor';
import { GlobalErrorHandlingService } from './core/services/global-error-handling.service';
import { FeedbackComponent } from './feedback/feedback.component';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { LegislationComponent } from './legislation/legislation.component';
import { markdownModuleConfig } from './shared/markdown/markdown-options';
import { SharedModule } from './shared/shared.module';
import { TermsAndConditionsComponent } from './terms-and-conditions/terms-and-conditions.component';
import { TimeoutModule } from './timeout/timeout.module';
import { VersionComponent } from './version/version.component';

const keycloakService = new KeycloakService();

@NgModule({
  declarations: [
    AccessibilityComponent,
    AppComponent,
    ContactUsComponent,
    CookiesContainerComponent,
    FeedbackComponent,
    LandingPageComponent,
    LegislationComponent,
    TermsAndConditionsComponent,
    VersionComponent,
  ],
  imports: [
    ApiModule.forRoot(() => new Configuration({ basePath: environment.apiOptions.baseUrl })),
    AppRoutingModule,
    BrowserModule,
    KeycloakAngularModule,
    MarkdownModule.forRoot(markdownModuleConfig),
    SharedModule,
    TimeoutModule,
  ],
  providers: [
    {
      provide: APP_BASE_HREF,
      useFactory: (pl: PlatformLocation) => pl.getBaseHrefFromDOM(),
      deps: [PlatformLocation],
    },
    {
      provide: KeycloakService,
      useValue: keycloakService,
    },
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandlingService,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: PendingRequestInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AnalyticsInterceptor,
      multi: true,
    },
    Title,
  ],
})
export class AppModule implements DoBootstrap {
  ngDoBootstrap(appRef: ApplicationRef): void {
    const authService = appRef.injector.get(AuthService);
    const configService = appRef.injector.get(ConfigService);
    const latestTermsService = appRef.injector.get(LatestTermsService);
    firstValueFrom(configService.initConfigState())
      .then((state) => {
        const options: KeycloakOptions = {
          ...environment.keycloakOptions,
          config: {
            ...(environment.keycloakOptions.config as KeycloakConfig),
            url: state.keycloakServerUrl ?? (environment.keycloakOptions.config as KeycloakConfig).url,
          },
        };
        return keycloakService.init(options);
      })
      .then(() => firstValueFrom(authService.checkUser()))
      .then(() => firstValueFrom(latestTermsService.initLatestTerms()))
      .then(() => firstValueFrom(combineLatest([configService.getMeasurementId(), configService.getPropertyId()])))
      .then(([measurementId, propertyId]) => initializeGoogleAnalytics(measurementId, propertyId))
      .then(() => appRef.bootstrap(AppComponent))
      .catch((error) => console.error('[ngDoBootstrap] init Keycloak failed', error));
  }
}
