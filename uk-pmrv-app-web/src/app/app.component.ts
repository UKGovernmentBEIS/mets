import { Component, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';

import { combineLatest, filter, map, Observable, of, shareReplay, switchMap, take, takeUntil } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AnalyticsService } from '@core/services/analytics.service';
import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import {
  selectCurrentDomain,
  selectIsLoggedIn,
  selectSwitchingDomain,
  selectUserState,
} from '@core/store/auth/auth.selectors';
import { AccountType, hasDomainLogin } from '@core/store/auth/auth.state';
import { AuthStore } from '@core/store/auth/auth.store';
import { DocumentEventService } from '@shared/services/document-event.service';

import { NavigationItem, ScrollService } from 'govuk-components';

import { BulkDownloadService } from 'pmrv-api';

import { CookiesService } from './cookies/cookies.service';
import { SettingsVisibilityService } from './settings/core/settings-visibility.service';

interface Permissions {
  showRegulators: boolean;
  showVerifiers: boolean;
  showAuthorizedOperators: boolean;
  hasAccessBulkDownload: boolean;
  showSettings: boolean;
}

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-root',
  standalone: false,
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [DestroySubject],
})
export class AppComponent implements OnInit {
  rootUrl: string = location.origin;
  serviceGatewayUrl: string = this.rootUrl;
  permissions$: Observable<null | Permissions>;
  navigationItems$: Observable<null | NavigationItem[]>;
  isLoggedIn$ = this.authStore.pipe(selectIsLoggedIn, takeUntil(this.destroy$));
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  isAviation = toSignal(this.currentDomain$.pipe(map((domain) => domain === 'AVIATION')), { initialValue: false });
  domainUrlPrefix$ = this.currentDomain$.pipe(map((domain) => (domain === 'AVIATION' ? '/aviation' : '')));
  isGatewayServiceEnabled$ = this.configStore.pipe(selectIsFeatureEnabled('serviceGatewayEnabled'));
  isSettingsEnabled$ = this.configStore.pipe(selectIsFeatureEnabled('settings'));
  showCookiesBanner$ = this.cookiesService.accepted$.pipe(
    map((cookiesAccepted) => !cookiesAccepted),
    take(1),
  );

  private readonly userState$ = this.authStore.pipe(selectUserState, takeUntil(this.destroy$));
  private readonly switchingDomain$ = this.authStore.pipe(selectSwitchingDomain, takeUntil(this.destroy$));
  private readonly roleType$ = this.userState$.pipe(
    map((userState) => userState?.roleType),
    takeUntil(this.destroy$),
  );

  // Without the Gateway there is no "Change service" link to switch domain from, so the header offers
  // a stand-in that sends the same `?domain=` hint. It is only shown when the user has a login for the
  // other domain, i.e. when LandingPageGuard would honour the request.
  readonly domainSwitchTarget$: Observable<AccountType | null> = combineLatest([
    this.currentDomain$,
    this.userState$,
  ]).pipe(
    map(([currentDomain, userState]) => {
      if (!currentDomain) {
        return null;
      }
      const target: AccountType = currentDomain === 'AVIATION' ? 'INSTALLATION' : 'AVIATION';
      return hasDomainLogin(userState, target) ? target : null;
    }),
  );

  constructor(
    public readonly authStore: AuthStore,
    private readonly configStore: ConfigStore,
    public readonly authService: AuthService,
    private readonly _scroll: ScrollService,
    private readonly _documentEvent: DocumentEventService,
    private readonly titleService: Title,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly cookiesService: CookiesService,
    private readonly bulkDownloadService: BulkDownloadService,
    private readonly settingsVisibilityService: SettingsVisibilityService,
    private readonly analyticsService: AnalyticsService,
  ) {}

  ngOnInit(): void {
    const hasAccessBulkDownload$: Observable<boolean> = this.bulkDownloadService
      .hasAccessBulkDownload()
      .pipe(shareReplay(1));

    this.permissions$ = combineLatest([this.isLoggedIn$, this.currentDomain$, this.switchingDomain$]).pipe(
      switchMap(([isLoggedIn, currentDomain, switchingDomain]) =>
        isLoggedIn
          ? this.roleType$.pipe(
              take(1),
              switchMap((role) => {
                const isRegulator = role === 'REGULATOR';
                const isAviation = currentDomain === 'AVIATION';

                const hasAccessBulkDownloadForRole$ = isRegulator && !isAviation ? hasAccessBulkDownload$ : of(false);
                const showSettings$ =
                  isRegulator && currentDomain
                    ? this.settingsVisibilityService.hasAnyAccessibleSection(currentDomain)
                    : of(false);

                return combineLatest([
                  this.userState$.pipe(
                    map((userState) =>
                      ['DISABLED', 'TEMP_DISABLED'].includes(userState?.domainsLoginStatuses?.[currentDomain]),
                    ),
                  ),
                  of(isRegulator),
                  of(role === 'VERIFIER'),
                  this.userState$.pipe(
                    map(
                      (userState) =>
                        userState !== null &&
                        userState?.roleType === 'OPERATOR' &&
                        userState?.domainsLoginStatuses[currentDomain] !== 'NO_AUTHORITY',
                    ),
                  ),
                  this.userState$.pipe(
                    map(
                      (userState) =>
                        !!switchingDomain && userState?.domainsLoginStatuses?.[switchingDomain] === 'NO_AUTHORITY',
                    ),
                  ),
                  hasAccessBulkDownloadForRole$,
                  showSettings$,
                ]).pipe(
                  map(
                    ([
                      isDisabled,
                      showRegulators,
                      showVerifiers,
                      showAuthorizedOperators,
                      isUnauthorizedDomain,
                      hasAccessBulkDownload,
                      showSettings,
                    ]) =>
                      !isDisabled &&
                      !isUnauthorizedDomain &&
                      Object.values({ showRegulators, showVerifiers, showAuthorizedOperators }).some(Boolean) && {
                        showRegulators,
                        showVerifiers,
                        showAuthorizedOperators,
                        hasAccessBulkDownload,
                        showSettings,
                      },
                  ),
                );
              }),
            )
          : of(null),
      ),
    );

    this.navigationItems$ = combineLatest([this.permissions$, this.domainUrlPrefix$, this.isSettingsEnabled$]).pipe(
      map(([permissions, domainUrlPrefix, isSettingsEnabled]) => {
        if (!permissions) {
          return null;
        }

        const items: NavigationItem[] = [];

        if (permissions.showRegulators || permissions.showVerifiers || permissions.showAuthorizedOperators) {
          items.push({ href: `${domainUrlPrefix}/dashboard`, text: 'Tasks' });
          items.push({ href: `${domainUrlPrefix}/accounts`, text: 'Accounts' });
        }
        if (permissions.showRegulators) {
          items.push({ href: `${domainUrlPrefix}/user/regulators`, text: 'Service access' });
          items.push({ href: `${domainUrlPrefix}/templates`, text: 'Templates' });
          items.push({ href: `${domainUrlPrefix}/mi-reports`, text: 'MI Reports' });
        }
        if (permissions.showVerifiers) {
          items.push({ href: `${domainUrlPrefix}/user/verifiers`, text: 'Service access' });
        }
        if (permissions.showRegulators) {
          items.push({ href: `${domainUrlPrefix}/workflows/batch-variations`, text: 'Variations' });
        }
        if (permissions.hasAccessBulkDownload) {
          items.push({ href: `${domainUrlPrefix}/bulk-downloads`, text: 'Downloads' });
        }
        if (permissions.showRegulators && permissions.showSettings && isSettingsEnabled) {
          items.push({ href: `${domainUrlPrefix}/settings`, text: 'Settings' });
        }

        return items;
      }),
    );

    const appTitle = this.titleService.getTitle();

    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        map(() => {
          let child = this.route.firstChild;
          while (child.firstChild) {
            child = child.firstChild;
          }
          if (child.snapshot.data['pageTitle']) {
            return child.snapshot.data['pageTitle'];
          }
          return appTitle;
        }),
        takeUntil(this.destroy$),
      )
      .subscribe((title: string) => this.titleService.setTitle(`${title} - GOV.UK`));

    if (this.cookiesService.hasAnalyticsConsent()) {
      this.analyticsService.enableGoogleTagManager();
    }
  }

  async logout(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();
    await this.authService.logout();
  }
}
