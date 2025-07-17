import { APP_BASE_HREF } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import {
  mockAuthorityService,
  mockKeycloakService,
  mockTermsAndConditionsService,
  mockUsersService,
} from '@core/guards/mocks';
import { AccountType, AuthStore, DomainsLoginStatuses, UserState } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { GovukComponentsModule } from 'govuk-components';

import { AuthoritiesService, TermsAndConditionsService, UsersService } from 'pmrv-api';

import { LandingPageComponent } from './landing-page.component';

describe('LandingPageComponent', () => {
  let component: LandingPageComponent;
  let fixture: ComponentFixture<LandingPageComponent>;
  let authStore: AuthStore;
  let page: Page;

  class Page extends BasePage<LandingPageComponent> {
    get notLoggedInLandingPageLinks() {
      return this.queryAll<HTMLAnchorElement>('.govuk-button--start');
    }

    get installationLink() {
      return this.query<HTMLAnchorElement>('a[href="/installation-account"]');
    }

    get pageHeadingContent() {
      return this.query<HTMLElement>('app-page-heading').textContent.trim();
    }

    get paragraphContents() {
      return this.queryAll<HTMLElement>('p').map((item) => item.textContent.trim());
    }
  }

  const setUser = (
    roleType: UserState['roleType'],
    loginStatuses: DomainsLoginStatuses,
    lastLoginDomain: AccountType = null,
  ) => {
    authStore.setUserState({ roleType, domainsLoginStatuses: loginStatuses, lastLoginDomain });
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule],
      declarations: [LandingPageComponent],
      providers: [
        { provide: KeycloakService, useValue: mockKeycloakService },
        { provide: UsersService, useValue: mockUsersService },
        { provide: AuthoritiesService, useValue: mockAuthorityService },
        { provide: TermsAndConditionsService, useValue: mockTermsAndConditionsService },
        { provide: APP_BASE_HREF, useValue: '/installation-aviation/' },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setIsLoggedIn(false);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LandingPageComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the landing page buttons if not logged in', () => {
    expect(page.installationLink).toBeFalsy();
    expect(page.notLoggedInLandingPageLinks).toHaveLength(2);
  });

  it('should display not registered when is logged in and no role type exist', () => {
    authStore.setIsLoggedIn(true);
    setUser(null, null);

    expect(page.pageHeadingContent).toEqual(
      'You need to create a sign in or contact your regulator or administrator to create a sign in for you.',
    );
  });

  it('should only display installation application button to operators', () => {
    expect(page.installationLink).toBeFalsy();
    expect(page.notLoggedInLandingPageLinks).toHaveLength(2);

    authStore.setIsLoggedIn(true);
    setUser('OPERATOR', { INSTALLATION: 'NO_AUTHORITY' });

    expect(page.installationLink).toBeTruthy();

    setUser('OPERATOR', { INSTALLATION: 'DISABLED' });

    expect(page.installationLink).toBeTruthy();

    setUser('REGULATOR', { INSTALLATION: 'DISABLED' });

    expect(page.installationLink).toBeFalsy();

    setUser('VERIFIER', { INSTALLATION: 'TEMP_DISABLED' });

    expect(page.installationLink).toBeFalsy();
  });

  it(`should show disabled message when AVIATION status 'DISABLED' and last login domain is 'AVIATION'
  or no INSTALLATION`, () => {
    authStore.setIsLoggedIn(true);
    setUser(
      'OPERATOR',
      {
        AVIATION: 'DISABLED',
        INSTALLATION: 'ENABLED',
      },
      'AVIATION',
    );
    expect(page.pageHeadingContent).toEqual(
      'Your user account has been disabled. Please contact your admin to gain access to your account.',
    );

    setUser('OPERATOR', { AVIATION: 'DISABLED' });
    expect(page.pageHeadingContent).toEqual(
      'Your user account has been disabled. Please contact your admin to gain access to your account.',
    );
  });

  it(`should show disabled message when role='REGULATOR' and INSTALLATION status 'DISABLED' and (last login domain is
  'INSTALLATION' or no AVIATION)`, () => {
    authStore.setIsLoggedIn(true);
    setUser(
      'REGULATOR',
      {
        AVIATION: 'ENABLED',
        INSTALLATION: 'DISABLED',
      },
      'INSTALLATION',
    );
    expect(page.pageHeadingContent).toEqual(
      'Your user account has been disabled. Please contact your admin to gain access to your account.',
    );

    setUser('REGULATOR', { INSTALLATION: 'DISABLED' });
    expect(page.pageHeadingContent).toEqual(
      'Your user account has been disabled. Please contact your admin to gain access to your account.',
    );
  });

  it(`should show ACCEPTED message when user login status is 'ACCEPTED'`, () => {
    authStore.setIsLoggedIn(true);
    setUser('OPERATOR', { AVIATION: 'ACCEPTED', INSTALLATION: 'NO_AUTHORITY' });
    expect(page.pageHeadingContent).toEqual('Your user account needs to be activated.');
    expect(page.paragraphContents).toEqual([
      'Your user account must be activated before you can sign in to the UK ETS reporting service.',
      "If your account was created by your regulator, they will now activate your account. You'll receive an email once your account has been activated. Contact your regulator if your account has not been activated after 2 working days.",
      "If your account was created by an operator administrator, they will now activate your account. You'll receive an email once your account has been activated. Contact your administrator if you have any questions about your account activation.",
    ]);
  });

  it(`should show ACCEPTED message when user login status is 'ACCEPTED' for both domains`, () => {
    authStore.setIsLoggedIn(true);
    setUser('OPERATOR', { AVIATION: 'ACCEPTED', INSTALLATION: 'ACCEPTED' });
    expect(page.pageHeadingContent).toEqual('Your user account needs to be activated.');
    expect(page.paragraphContents).toEqual([
      'Your user account must be activated before you can sign in to the UK ETS reporting service.',
      "If your account was created by your regulator, they will now activate your account. You'll receive an email once your account has been activated. Contact your regulator if your account has not been activated after 2 working days.",
      "If your account was created by an operator administrator, they will now activate your account. You'll receive an email once your account has been activated. Contact your administrator if you have any questions about your account activation.",
    ]);
  });
});
