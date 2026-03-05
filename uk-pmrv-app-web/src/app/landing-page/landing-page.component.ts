import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, LoginStatus, onlyAssociatedWithDomain, UserState } from '@core/store/auth';
import { KeycloakProfile } from 'keycloak-js';

import { InstallationAccountApplicationStore } from '../installation-account-application/store/installation-account-application.store';

interface ViewModel {
  isLoggedIn: boolean;
  userProfile: KeycloakProfile;
  installationLogin: LoginStatus;
  aviationLogin: LoginStatus;
  roleType: UserState['roleType'];
  lastLoginDomain: UserState['lastLoginDomain'];
  switchingDomain: UserState['lastLoginDomain'];
  installationEnabled: boolean;
  hasOnlyInstallation: boolean;
  hasOnlyAviation: boolean;
  hasNoLogin: boolean;
  hasBothAccepted: boolean;
}

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class LandingPageComponent {
  vm$: Observable<ViewModel> = this.authStore.pipe(
    map(({ userState, userProfile, isLoggedIn, switchingDomain }) => ({
      userProfile,
      isLoggedIn,
      installationLogin: userState?.domainsLoginStatuses?.INSTALLATION,
      aviationLogin: userState?.domainsLoginStatuses?.AVIATION,
      roleType: userState?.roleType,
      lastLoginDomain: userState?.lastLoginDomain,
      switchingDomain,
      installationEnabled: userState?.domainsLoginStatuses?.INSTALLATION === 'ENABLED',
      hasOnlyInstallation: onlyAssociatedWithDomain('INSTALLATION', userState),
      hasOnlyAviation: onlyAssociatedWithDomain('AVIATION', userState),
      hasNoLogin: userState && Object.values(userState.domainsLoginStatuses).every((dls) => dls === 'NO_AUTHORITY'),
      hasBothAccepted: userState && Object.values(userState.domainsLoginStatuses).every((dls) => dls === 'ACCEPTED'),
    })),
  );

  constructor(
    private readonly authStore: AuthStore,
    public readonly authService: AuthService,
    public readonly store: InstallationAccountApplicationStore,
  ) {}
}
