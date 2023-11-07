import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, map, of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { AccountStatusPipe } from '@shared/pipes/account-status.pipe';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAccountsService, InstallationAccountsService } from 'pmrv-api';

import { AccountsListComponent } from '../../components';
import { AccountsService } from '../../services';
import { AccountsStore } from '../../store';
import { mockAccountResults, operatorUserRole, regulatorUserRole } from '../../testing';
import { AccountsPageComponent } from './';

describe('AccountsComponent', () => {
  let component: AccountsPageComponent;
  let fixture: ComponentFixture<AccountsPageComponent>;
  let page: Page;
  let authStore: AuthStore;
  const accountsService = mockClass(AccountsService);
  const installationAccountsService = mockClass(InstallationAccountsService);
  const aviationAccountsService = mockClass(AviationAccountsService);

  class Page extends BasePage<AccountsPageComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading');
    }

    set termValue(value: string) {
      this.setInputValue('#term', value);
    }
    get termErrorMessage() {
      return this.query<HTMLElement>('div[formcontrolname="term"] span.govuk-error-message');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get accounts() {
      return this.queryAll<HTMLLIElement>('form#search-form ul.govuk-list > li');
    }

    get accountNames() {
      return this.queryAll<HTMLLIElement>('form#search-form ul.govuk-list > li a');
    }

    get accountStatuses() {
      return this.queryAll<HTMLSpanElement>('.status-tag');
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(AccountsPageComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    jest.clearAllMocks();
  };

  const createModule = async () => {
    await TestBed.configureTestingModule({
      declarations: [AccountsPageComponent, AccountsListComponent, AccountStatusPipe, PageHeadingComponent],
      imports: [ReactiveFormsModule, RouterTestingModule, GovukComponentsModule],
      providers: [
        AccountsStore,
        { provide: InstallationAccountsService, useValue: installationAccountsService },
        { provide: AviationAccountsService, useValue: aviationAccountsService },
        { provide: AccountsService, useValue: accountsService },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');
  };

  describe('for operators', () => {
    beforeEach(async () => {
      accountsService.getCurrentUserAccounts.mockReturnValueOnce(of(mockAccountResults));
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    beforeEach(async () => {
      authStore.setUserState(operatorUserRole);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the heading', async () => {
      expect(page.heading.textContent.trim()).toEqual('Accounts');
    });

    it('should show results upon loading the page', () => {
      expect(page.accountNames.map((accountName) => accountName.textContent.trim())).toEqual([
        'account1',
        'account2',
        'account3',
      ]);
    });

    it('should show error when term less than 3 characters and pressing search button', async () => {
      page.termValue = 'te';
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.termErrorMessage.textContent.trim()).toContain('Enter at least 3 characters');
    });

    it('should show accounts when term filled and pressing search button', async () => {
      page.termValue = 'term';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.termErrorMessage).toBeNull();
      expect(accountsService.getCurrentUserAccounts).toHaveBeenCalledTimes(2);
      expect(accountsService.getCurrentUserAccounts).toHaveBeenLastCalledWith(0, 30, 'term');
      expect(page.accountNames.map((accountName) => accountName.textContent.trim())).toEqual([
        'account1',
        'account2',
        'account3',
      ]);
      expect(page.accountStatuses.map((accountStatus) => accountStatus.textContent.trim())).toEqual([
        'Live',
        'Live',
        'Live',
      ]);
    });
  });

  describe('for non operator users', () => {
    beforeEach(() => {
      accountsService.getCurrentUserAccounts.mockReturnValue(of(mockAccountResults));
    });
    beforeEach(createModule);
    beforeEach(createComponent);

    beforeEach(async () => {
      authStore.setUserState(regulatorUserRole);
      fixture.detectChanges();
    });

    it('should create', async () => {
      expect(component).toBeTruthy();
    });

    it('should render the heading', async () => {
      expect(page.heading.textContent.trim()).toEqual('Accounts');
    });

    it('should show accounts when term filled and pressing search button', async () => {
      page.termValue = 'term';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.termErrorMessage).toBeNull();
      expect(accountsService.getCurrentUserAccounts).toHaveBeenCalledTimes(2);
      expect(accountsService.getCurrentUserAccounts).toHaveBeenLastCalledWith(0, 30, 'term');
      expect(page.accountNames.map((accountName) => accountName.textContent.trim())).toEqual([
        'account1',
        'account2',
        'account3',
      ]);
      expect(page.accountStatuses.map((accountStatus) => accountStatus.textContent.trim())).toEqual([
        'Live',
        'Live',
        'Live',
      ]);
    });
  });

  describe('for regulators with search params set', () => {
    const activatedRouteStub = new ActivatedRouteStub(
      undefined,
      {
        term: 'account',
      },
      undefined,
    );

    beforeEach(async () => {
      accountsService.getCurrentUserAccounts.mockReturnValueOnce(of(mockAccountResults));
    });
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [AccountsPageComponent, AccountsListComponent, AccountStatusPipe, PageHeadingComponent],
        imports: [RouterTestingModule, ReactiveFormsModule, GovukComponentsModule],
        providers: [
          AccountsStore,
          { provide: InstallationAccountsService, useValue: installationAccountsService },
          { provide: AviationAccountsService, useValue: aviationAccountsService },
          { provide: AccountsService, useValue: accountsService },
          { provide: ActivatedRoute, useValue: activatedRouteStub },
          { provide: AccountsService, useValue: accountsService },
        ],
      }).compileComponents();

      authStore = TestBed.inject(AuthStore);
    });
    beforeEach(createComponent);
    beforeEach(async () => {
      authStore.setUserState(regulatorUserRole);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should load the accounts based on the query params', () => {
      expect(page.accountNames.map((accountName) => accountName.textContent.trim())).toEqual([
        'account1',
        'account2',
        'account3',
      ]);
      expect(page.accountStatuses.map((accountStatus) => accountStatus.textContent.trim())).toEqual([
        'Live',
        'Live',
        'Live',
      ]);
    });
  });

  describe('for verifier users without accounts appointed to verification body', () => {
    beforeEach(async () => {
      accountsService.getCurrentUserAccounts.mockReturnValue(of({ accounts: [], total: 0 }));
    });
    beforeEach(createModule);
    beforeEach(() => {
      authStore.setUserState({
        ...authStore.getState().userState,
        domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
        roleType: 'VERIFIER',
        userId: 'asdf4',
      });
    });
    beforeEach(async () => {
      await createComponent();
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the appropriate heading', async () => {
      await expect(firstValueFrom(component.vm$.pipe(map((vm) => vm.userRoleType)))).resolves.toEqual('VERIFIER');
      expect(page.heading.textContent.trim()).toEqual('There are no accounts to view.');
    });
  });
});
