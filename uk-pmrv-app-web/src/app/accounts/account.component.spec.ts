import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store/auth';
import { OperatorsComponent, WorkflowsComponent } from '@shared/accounts';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { SharedUserModule } from '../shared-user/shared-user.module';
import { AccountComponent } from './account.component';
import { DetailsComponent } from './details/details.component';
import { mockedAccountPermit } from './testing/mock-data';

describe('AccountComponent', () => {
  let component: AccountComponent;
  let fixture: ComponentFixture<AccountComponent>;
  let authStore: AuthStore;
  let page: Page;
  let activatedRouteStub: ActivatedRouteStub;
  let authService: Partial<jest.Mocked<AuthService>>;

  activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
    accountPermit: mockedAccountPermit,
  });

  class Page extends BasePage<AccountComponent> {
    get notification() {
      return this.query<HTMLElement>('govuk-notification-banner div.govuk-notification-banner');
    }

    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-xl');
    }

    get status() {
      return this.heading.querySelector<HTMLSpanElement>('span.status');
    }

    get tabs() {
      return Array.from(this.queryAll<HTMLLIElement>('ul.govuk-tabs__list > li'));
    }
  }

  beforeEach(async () => {
    authService = {
      loadUserState: jest.fn(),
    };

    await TestBed.configureTestingModule({
      declarations: [AccountComponent, DetailsComponent, WorkflowsComponent, OperatorsComponent],
      imports: [RouterTestingModule, SharedModule, SharedUserModule],
      providers: [
        DestroySubject,
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: AuthService, useValue: authService },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({
      ...authStore.getState().userState,
      domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
      roleType: 'OPERATOR',
      userId: 'opTestId',
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountComponent);
    component = fixture.componentInstance;
    window.history.pushState({ accountTypes: 'INSTALLATION', page: '1' }, 'yes');
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the account name', () => {
    expect(page.heading.textContent.trim()).toContain(mockedAccountPermit.account.name);
  });

  it('should render the status', () => {
    expect(page.status.textContent.trim()).toEqual('Live');
  });

  describe('for operators', () => {
    beforeEach(() => {
      authStore.setUserState({
        ...authStore.getState().userState,
        roleType: 'OPERATOR',
        userId: 'opTestId',
      });
      fixture.detectChanges();
    });

    it('should render all tabs', () => {
      expect(page.tabs.map((tab) => tab.textContent.trim())).toEqual([
        'Details',
        'Permit history',
        'Reports',
        'Users and contacts',
      ]);
    });

    it("should not have a WASTE notification if it's not a WASTE emitterType", () => {
      expect(page.notification).toBeNull();
    });

    describe('with emitterType equal to WASTE', () => {
      beforeEach(async () => {
        const accountPermit = {
          ...mockedAccountPermit,
          account: { ...mockedAccountPermit.account, status: 'LIVE', emitterType: 'WASTE' },
        };

        activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
          accountPermit,
        });

        authStore.setUserState({
          ...authStore.getState().userState,
          roleType: 'OPERATOR',
          userId: 'opTestId',
        });
        fixture.detectChanges();
      });

      it('should render all tabs', () => {
        expect(page.tabs.map((tab) => tab.textContent.trim())).toEqual([
          'Details',
          'Permit history',
          'Reports',
          'Users and contacts',
        ]);
      });

      it('should have a WASTE notification when the role type is an OPERATOR', () => {
        expect(page.notification?.textContent.trim()).toEqual(
          'Important During the voluntary waste monitoring, reporting and verification period, the METS service will reflect the full ETS scheme, so some references and data requests may not be relevant to waste operators. Contact your regulator for further information on how to participate.',
        );
      });

      it('should not have a WASTE notification when the role type is a REGULATOR ', () => {
        authStore.setUserState({
          ...authStore.getState().userState,
          roleType: 'REGULATOR',
          userId: 'opTestId',
        });
        fixture.detectChanges();

        expect(page.notification).toBeNull();
      });
    });
  });

  describe('for regulators', () => {
    beforeEach(() => {
      authStore.setUserState({
        ...authStore.getState().userState,
        roleType: 'REGULATOR',
        userId: 'opTestId',
      });
      fixture.detectChanges();
    });

    it('should render all tabs', () => {
      expect(page.tabs.map((tab) => tab.textContent.trim())).toEqual([
        'Details',
        'Notes',
        'Permit history',
        'Reports',
        'Users and contacts',
      ]);
    });
  });

  describe('for verifiers', () => {
    beforeEach(() => {
      authStore.setUserState({
        ...authStore.getState().userState,
        roleType: 'VERIFIER',
        userId: 'opTestId',
      });
      fixture.detectChanges();
    });

    it('should not render users tab', () => {
      expect(page.tabs.map((tab) => tab.textContent.trim())).toEqual(['Details', 'Permit history', 'Reports']);
    });
  });
});
