import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';

import { combineLatest, filter, map, Observable, of, switchMap, takeUntil } from 'rxjs';

import { gtagIsAvailable, toggleAnalytics } from '@core/analytics';
import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { UserState } from '@core/store/auth';
import {
  selectCanSwitchDomain,
  selectCurrentDomain,
  selectIsLoggedIn,
  selectSwitchingDomain,
  selectUserState,
} from '@core/store/auth/auth.selectors';
import { AuthStore } from '@core/store/auth/auth.store';
import { DocumentEventService } from '@shared/services/document-event.service';

import { ScrollService } from 'govuk-components';

import { CookiesService } from './cookies/cookies.service';

interface Permissions {
  showRegulators: boolean;
  showVerifiers: boolean;
  showAuthorizedOperators: boolean;
}

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [DestroySubject],
})
export class AppComponent implements OnInit {
  rootUrl: string = location.origin;
  serviceGatewayUrl: string = this.rootUrl;
  permissions$: Observable<null | Permissions>;
  isLoggedIn$ = this.authStore.pipe(selectIsLoggedIn, takeUntil(this.destroy$));
  canSwitchDomain$ = combineLatest([
    this.authStore.pipe(selectCanSwitchDomain, takeUntil(this.destroy$)),
    this.configStore.pipe(selectIsFeatureEnabled('aviation')),
  ]).pipe(map(([canSwitchDomain, aviationEnabled]) => canSwitchDomain && aviationEnabled));
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  domainUrlPrefix$ = this.currentDomain$.pipe(map((domain) => (domain === 'AVIATION' ? '/aviation' : '')));
  isGatewayServiceEnabled$ = this.configStore.pipe(selectIsFeatureEnabled('serviceGatewayEnabled'));
  showCookiesBanner$ = this.cookiesService.accepted$.pipe(
    map((cookiesAccepted) => !cookiesAccepted && gtagIsAvailable()),
  );
  currentSelection: WritableSignal<UserState['lastLoginDomain']> = signal(null);

  private readonly userState$ = this.authStore.pipe(selectUserState, takeUntil(this.destroy$));
  private readonly switchingDomain$ = this.authStore.pipe(selectSwitchingDomain, takeUntil(this.destroy$));
  private readonly roleType$ = this.userState$.pipe(
    map((userState) => userState?.roleType),
    takeUntil(this.destroy$),
  );
  private interactedByKeyboard = false;

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
  ) {}

  ngOnInit(): void {
    this.currentDomain$.subscribe((domain) => {
      this.currentSelection.set(domain);
    });

    this.permissions$ = combineLatest([this.isLoggedIn$, this.currentDomain$, this.switchingDomain$]).pipe(
      switchMap(([isLoggedIn, currentDomain, switchingDomain]) =>
        isLoggedIn
          ? combineLatest([
              this.userState$.pipe(
                map((userState) => {
                  return ['DISABLED', 'TEMP_DISABLED'].includes(userState?.domainsLoginStatuses?.[currentDomain]);
                }),
              ),
              this.roleType$.pipe(map((role) => role === 'REGULATOR')),
              this.roleType$.pipe(map((role) => role === 'VERIFIER')),
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
                    currentDomain === 'AVIATION' &&
                    switchingDomain === 'INSTALLATION' &&
                    userState?.domainsLoginStatuses[switchingDomain] === 'NO_AUTHORITY',
                ),
              ),
            ]).pipe(
              map(
                ([isDisabled, showRegulators, showVerifiers, showAuthorizedOperators, isUnauthorizedDomain]) =>
                  !isDisabled &&
                  !isUnauthorizedDomain &&
                  Object.values({ showRegulators, showVerifiers, showAuthorizedOperators }).some(
                    (permission) => !!permission,
                  ) && {
                    showRegulators,
                    showVerifiers,
                    showAuthorizedOperators,
                  },
              ),
            )
          : of(null),
      ),
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

    if (this.cookiesService.accepted$.getValue()) {
      toggleAnalytics(true);
    }
  }

  async logout(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();
    await this.authService.logout();
  }

  onSelectionChangeDomainSwitch(domain: UserState['lastLoginDomain']) {
    this.currentSelection.set(domain);

    // Only navigate if interaction was by mouse (click)
    if (!this.interactedByKeyboard) {
      this.confirmDomainSwitch();
    }
  }

  onKeyDownDomainSwitch(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.interactedByKeyboard = true;
      this.confirmDomainSwitch();
      this.interactedByKeyboard = false;
    } else {
      this.interactedByKeyboard = true;
    }
  }

  onClickDomainSwitch() {
    this.interactedByKeyboard = false;
  }

  confirmDomainSwitch() {
    // Now navigate explicitly
    if (this.currentSelection() === 'INSTALLATION') {
      this.authStore.setSwitchingDomain('INSTALLATION');
      this.router.navigate(['dashboard']);
    } else if (this.currentSelection() === 'AVIATION') {
      this.authStore.setSwitchingDomain('AVIATION');
      this.router.navigate(['aviation', 'dashboard']);
    }
  }
}
