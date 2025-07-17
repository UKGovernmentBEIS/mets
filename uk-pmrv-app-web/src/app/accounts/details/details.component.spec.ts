import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { CountryService } from '@core/services/country.service';
import { AuthStore, UserState } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, CountryServiceStub, mockClass } from '@testing';

import { CompanyInformationService } from 'pmrv-api';

import { SharedUserModule } from '../../shared-user/shared-user.module';
import { mockedAccountPermit } from '../testing/mock-data';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let page: Page;
  let authStore: AuthStore;
  let authService: Partial<jest.Mocked<AuthService>>;
  let activatedRouteStub: ActivatedRouteStub;
  const companyInformationService = mockClass(CompanyInformationService);

  class Page extends BasePage<DetailsComponent> {
    get heading() {
      return this.queryAll<HTMLHeadingElement>('h2');
    }

    get accountDetails() {
      return this.queryAll<HTMLElement>('dl dd:not(.govuk-summary-list__actions)');
    }

    get actions() {
      return this.queryAll<HTMLElement>('dl dd.govuk-summary-list__actions');
    }

    get checkboxes() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }
  }

  const createModule = async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailsComponent],
      imports: [RouterTestingModule, SharedModule, SharedUserModule],
      providers: [
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: AuthService, useValue: authService },
        { provide: CompanyInformationService, useValue: companyInformationService },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);

    companyInformationService.getCompanyProfileByRegistrationNumber.mockReturnValue(
      of({
        name: 'COMPANY 91634248 LIMITED',
        registrationNumber: '91634248',
        address: {
          line1: 'Companies House',
          line2: 'Crownway',
          city: 'Cardiff',
          country: 'United Kingdom',
          postcode: 'CF14 3UZ',
        },
      }),
    );
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    component.currentTab = 'details';
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('approved account for operators', () => {
    beforeEach(async () => {
      activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
        accountPermit: mockedAccountPermit,
      });

      authService = {
        loadUserState: jest.fn(),
      };
    });

    beforeEach(createModule);
    beforeEach(() => setUser('OPERATOR'));
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the headings', () => {
      expect(page.heading.map((el) => el.textContent.trim())).toEqual([
        'Active permit',
        'Installation details',
        'Organisation details',
      ]);
    });

    it('should render the account details', () => {
      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        'permitDoc.pdf Attached documents att1.pdf  att2.pdf',
        '1 Jan 2023',
        'permitId',

        'accountName',
        'siteName',
        '222',
        '111',
        'GHGE',
        'Category A (Low emitter)',
        'A',
        'New Permit',
        'NN166712 line town1231Greece',
        'Yes',

        '11111',
        'leName',
        'Limited Company',
        'line town1231Greece',

        'TEST_HC',
        '',
        'TEST_REG_NUM',
        'TEST_ADDR_LINE1 TEST_CITYTEST_POSTCODE',
      ]);
    });

    it('should render the edit links', () => {
      expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(0);
    });
  });

  describe('unapproved account for operators', () => {
    beforeEach(async () => {
      const accountPermit = {
        ...mockedAccountPermit,
        account: { ...mockedAccountPermit.account, status: 'UNAPPROVED' },
        permit: null,
      };

      activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
        accountPermit: accountPermit,
      });

      authService = {
        loadUserState: jest.fn(),
      };
    });

    beforeEach(createModule);
    beforeEach(() => setUser('OPERATOR'));
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not render the active permit', () => {
      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        'accountName',
        'siteName',
        '222',
        '111',
        'GHGE',
        'Category A (Low emitter)',
        'A',
        'New Permit',
        'NN166712 line town1231Greece',
        'Yes',

        '11111',
        'leName',
        'Limited Company',
        'line town1231Greece',

        'TEST_HC',
        '',
        'TEST_REG_NUM',
        'TEST_ADDR_LINE1 TEST_CITYTEST_POSTCODE',
      ]);
    });

    it('should not render the edit links', () => {
      expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(0);
    });
  });

  describe('approved account for regulators', () => {
    beforeEach(async () => {
      activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
        accountPermit: mockedAccountPermit,
      });

      authService = {
        loadUserState: jest.fn(),
      };
    });

    beforeEach(createModule);
    beforeEach(() => setUser('REGULATOR'));
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the headings', () => {
      expect(page.heading.map((el) => el.textContent.trim())).toEqual([
        'Active permit',
        'Installation details',
        'Organisation details',
      ]);
    });

    it('should render the account details', () => {
      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        'permitDoc.pdf Attached documents att1.pdf  att2.pdf',
        '1 Jan 2023',
        'permitId',

        'accountName',
        'siteName',
        '222',
        '111',
        'GHGE',
        'Category A (Low emitter)',
        'A',
        'New Permit',
        'NN166712 line town1231Greece',
        'Yes',

        '11111',
        'leName',
        'Limited Company',
        'line town1231Greece',

        'TEST_HC',
        '',
        'TEST_REG_NUM',
        'TEST_ADDR_LINE1 TEST_CITYTEST_POSTCODE',
      ]);
    });

    it('should render the edit links', () => {
      expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(13);
    });

    it('should render the companies house details', () => {
      page.checkboxes[0].click();
      fixture.detectChanges();

      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        'permitDoc.pdf Attached documents att1.pdf  att2.pdf',
        '1 Jan 2023',
        'permitId',

        'accountName',
        'siteName',
        '222',
        '111',
        'GHGE',
        'Category A (Low emitter)',
        'A',
        'New Permit',
        'NN166712 line town1231Greece',
        'Yes',
        '11111',
        '91634248',
        'leName',
        'COMPANY 91634248 LIMITED',
        'Limited Company',
        '',
        'line town1231Greece',
        'Companies House  , Crownway CardiffCF14 3UZUnited Kingdom',
        'TEST_HC',
        '',
        'TEST_REG_NUM',
        '',
        'TEST_ADDR_LINE1 TEST_CITYTEST_POSTCODE',
        '',
      ]);
    });
  });

  describe('unapproved account for operators', () => {
    beforeEach(async () => {
      const accountPermit = {
        ...mockedAccountPermit,
        account: { ...mockedAccountPermit.account, status: 'UNAPPROVED' },
        permit: null,
      };

      activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
        accountPermit: accountPermit,
      });

      authService = {
        loadUserState: jest.fn(),
      };
    });

    beforeEach(createModule);
    beforeEach(() => setUser('REGULATOR'));
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not render the active permit', () => {
      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        'accountName',
        'siteName',
        '222',
        '111',
        'GHGE',
        'Category A (Low emitter)',
        'A',
        'New Permit',
        'NN166712 line town1231Greece',
        'Yes',

        '11111',
        'leName',
        'Limited Company',
        'line town1231Greece',

        'TEST_HC',
        '',
        'TEST_REG_NUM',
        'TEST_ADDR_LINE1 TEST_CITYTEST_POSTCODE',
      ]);
    });

    it('should not render the edit links', () => {
      expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(0);
    });
  });

  describe('denied account for operators', () => {
    beforeEach(async () => {
      const accountPermit = {
        ...mockedAccountPermit,
        account: { ...mockedAccountPermit.account, status: 'DENIED' },
        permit: null,
      };

      activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
        accountPermit: accountPermit,
      });

      authService = {
        loadUserState: jest.fn(),
      };
    });

    beforeEach(createModule);
    beforeEach(() => setUser('REGULATOR'));
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not render the active permit', () => {
      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        'accountName',
        'siteName',
        '222',
        '111',
        'GHGE',
        'Category A (Low emitter)',
        'A',
        'New Permit',
        'NN166712 line town1231Greece',
        'Yes',

        '11111',
        'leName',
        'Limited Company',
        'line town1231Greece',

        'TEST_HC',
        '',
        'TEST_REG_NUM',
        'TEST_ADDR_LINE1 TEST_CITYTEST_POSTCODE',
      ]);
    });

    it('should not render the edit links', () => {
      expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(0);
    });
  });

  function setUser(role: UserState['roleType']) {
    authStore.setUserState({
      ...authStore.getState().userState,
      domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
      roleType: role,
      userId: role === 'REGULATOR' ? 'regUserId' : 'opTestId',
    });
  }
});
