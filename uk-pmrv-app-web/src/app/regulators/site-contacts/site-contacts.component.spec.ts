import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { throwError } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, BasePage, MockType } from '@testing';

import {
  AccountContactInfoResponse,
  CaSiteContactsService,
  RegulatorAuthoritiesService,
  RegulatorUsersAuthoritiesInfoDTO,
} from 'pmrv-api';

import { savePartiallyNotFoundSiteContactError } from '../errors/business-error';
import { SiteContactsComponent } from './site-contacts.component';

describe('SiteContactsComponent', () => {
  let component: SiteContactsComponent;
  let fixture: ComponentFixture<SiteContactsComponent>;
  let page: Page;
  let activatedRoute: ActivatedRouteStub;
  let authStore: AuthStore;
  let router: Router;

  class Page extends BasePage<SiteContactsComponent> {
    get accounts() {
      return this.queryAll<HTMLTableCellElement>('tbody > tr > th');
    }

    get assignees() {
      return this.queryAll<HTMLTableCellElement>('tbody > tr > td');
    }

    get assigneeSelects() {
      return this.queryAll<HTMLSelectElement>('tbody select');
    }

    get assigneeSelectValues() {
      return this.assigneeSelects.map((select) => page.getInputValue(`#${select.id}`));
    }

    set assigneeSelectValues(values: string[]) {
      this.assigneeSelects.forEach((select, index) => this.setInputValue(`#${select.id}`, values[index]));
    }

    get currentPage() {
      return this.query<HTMLLIElement>('.govuk-pagination__item--current');
    }

    get saveButton() {
      return this.query<HTMLButtonElement>('#site-contacts-form button[type="submit"]');
    }

    get errorList() {
      return this.queryAll<HTMLLIElement>('.govuk-error-summary__list li');
    }

    set termValue(value: string) {
      this.setInputValue('#term', value);
    }

    get termErrorMessage() {
      return this.query<HTMLElement>('div[formcontrolname="term"] span.govuk-error-message');
    }

    get searchButton() {
      return this.query<HTMLButtonElement>('#site-contacts-search-form button[type="submit"]');
    }

    get noResultsMessage() {
      return this.query<HTMLParagraphElement>('p.govuk-body');
    }
  }

  const regulators: RegulatorUsersAuthoritiesInfoDTO = {
    caUsers: [
      {
        userId: 'ax6asd',
        authorityCreationDate: '2021-02-16T14:03:01.000Z',
        authorityStatus: 'ACTIVE',
        firstName: 'Bob',
        lastName: 'Squarepants',
        jobTitle: 'Swimmer',
      },
      {
        userId: 'bsdfg3',
        authorityCreationDate: '2021-02-16T12:03:01.000Z',
        authorityStatus: 'ACTIVE',
        firstName: 'Patrick',
        lastName: 'Star',
        jobTitle: 'Funny guy',
      },
      {
        userId: 'bGFDFG',
        authorityCreationDate: '2021-02-13T11:33:01.000Z',
        authorityStatus: 'PENDING',
        firstName: 'Tes',
        lastName: 'Locke',
        jobTitle: 'Officer',
      },
    ],
    editable: true,
  };
  const siteContacts: AccountContactInfoResponse = {
    contacts: [
      { accountName: 'Test facility', accountId: 1, userId: regulators.caUsers[0].userId },
      { accountName: 'Dev facility', accountId: 2 },
    ],
    editable: true,
    totalItems: 2,
  };

  const siteContactsService: jest.Mocked<Partial<CaSiteContactsService>> = {
    getCaSiteContacts: jest.fn().mockReturnValue(asyncData(siteContacts)),
    updateCaSiteContacts: jest.fn().mockReturnValue(asyncData(null)),
  };

  const regulatorAuthoritiesService: MockType<RegulatorAuthoritiesService> = {
    getCaRegulators: jest.fn().mockReturnValue(asyncData(regulators)),
  };

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub();

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule, RouterTestingModule, BusinessTestingModule],
      declarations: [SiteContactsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: CaSiteContactsService, useValue: siteContactsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
      ],
    }).compileComponents();
  });

  const createComponent = async () => {
    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(SiteContactsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(createComponent);

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have called data services only if tab is active', () => {
    expect(siteContactsService.getCaSiteContacts).not.toHaveBeenCalled();

    activatedRoute.setFragment('site-contacts');

    expect(siteContactsService.getCaSiteContacts).toHaveBeenCalledTimes(1);
    expect(siteContactsService.getCaSiteContacts).toHaveBeenCalledWith('INSTALLATION', 0, 50, null);
  });

  it('should display the list of accounts with their assignees', async () => {
    activatedRoute.setFragment('site-contacts');
    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.accounts.map((header) => header.textContent)).toEqual(['Dev facility', 'Test facility']);
    expect(page.assigneeSelectValues).toEqual([null, 'ax6asd']);
    expect(page.assigneeSelects.map((select) => select.selectedOptions[0].textContent.trim())).toEqual([
      'Unassigned',
      'Bob Squarepants',
    ]);
  });

  it('should not show a Type column', async () => {
    activatedRoute.setFragment('site-contacts');
    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.query('thead').textContent).not.toContain('Type');
  });

  it('should show an inline error and not navigate when the term is fewer than 3 characters', async () => {
    activatedRoute.setFragment('site-contacts');
    await fixture.whenStable();
    fixture.detectChanges();
    const navigateSpy = jest.spyOn(router, 'navigate');
    jest.clearAllMocks();

    page.termValue = 'te';
    page.searchButton.click();
    fixture.detectChanges();

    expect(page.termErrorMessage.textContent).toContain('Enter at least 3 characters');
    expect(navigateSpy).not.toHaveBeenCalled();
    expect(siteContactsService.getCaSiteContacts).not.toHaveBeenCalled();
  });

  it('should navigate with the term and reset the page on search', async () => {
    activatedRoute.setFragment('site-contacts');
    await fixture.whenStable();
    fixture.detectChanges();
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.termValue = 'facility';
    page.searchButton.click();
    fixture.detectChanges();

    expect(page.termErrorMessage).toBeNull();
    expect(navigateSpy).toHaveBeenCalledWith([], {
      relativeTo: activatedRoute,
      preserveFragment: true,
      queryParams: { term: 'facility', page: null },
      queryParamsHandling: 'merge',
    });
  });

  it('should search by the term present in the URL', async () => {
    activatedRoute.setQueryParamMap({ term: 'facility' });
    activatedRoute.setFragment('site-contacts');
    await fixture.whenStable();
    fixture.detectChanges();

    expect(siteContactsService.getCaSiteContacts).toHaveBeenLastCalledWith('INSTALLATION', 0, 50, 'facility');
    expect(page.termErrorMessage).toBeNull();
  });

  it('should show a no-results message when the search returns no accounts', async () => {
    siteContactsService.getCaSiteContacts.mockReturnValueOnce(
      asyncData({ contacts: [], editable: true, totalItems: 0 }),
    );
    activatedRoute.setFragment('site-contacts');
    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.noResultsMessage.textContent).toContain(
      'No matching accounts found. Try searching for a different account name, or clear the search to view all accounts.',
    );
    expect(page.accounts).toEqual([]);
  });

  it('should show the full list again once the term is cleared in the URL', async () => {
    activatedRoute.setQueryParamMap({ term: 'facility' });
    activatedRoute.setFragment('site-contacts');
    await fixture.whenStable();
    fixture.detectChanges();

    activatedRoute.setQueryParamMap({});
    await fixture.whenStable();
    fixture.detectChanges();

    expect(siteContactsService.getCaSiteContacts).toHaveBeenLastCalledWith('INSTALLATION', 0, 50, null);
  });

  it('should save the updated assignees', async () => {
    activatedRoute.setFragment('site-contacts');
    await fixture.whenStable();
    fixture.detectChanges();

    page.assigneeSelectValues = ['bsdfg3', null];
    fixture.detectChanges();

    page.saveButton.click();
    fixture.detectChanges();

    expect(siteContactsService.updateCaSiteContacts).toHaveBeenCalledWith('INSTALLATION', [
      { accountId: 2, userId: 'bsdfg3' },
      { accountId: 1, userId: null },
    ]);
  });

  it('should show error page in case the authority has been deleted meanwhile', async () => {
    siteContactsService.updateCaSiteContacts.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'AUTHORITY1003' }, status: 400 })),
    );
    activatedRoute.setFragment('site-contacts');

    await fixture.whenStable();
    fixture.detectChanges();

    page.assigneeSelectValues = ['bsdfg3', null];
    fixture.detectChanges();
    page.saveButton.click();

    fixture.detectChanges();

    await expectBusinessErrorToBe(savePartiallyNotFoundSiteContactError);
  });

  it('should show error page in case the user has been deleted meanwhile', async () => {
    siteContactsService.updateCaSiteContacts.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'ACCOUNT1004' }, status: 400 })),
    );
    activatedRoute.setFragment('site-contacts');

    await fixture.whenStable();
    fixture.detectChanges();

    page.assigneeSelectValues = ['bsdfg3', null];
    fixture.detectChanges();
    page.saveButton.click();

    fixture.detectChanges();

    await expectBusinessErrorToBe(savePartiallyNotFoundSiteContactError);
  });

  it('should display assignees as plain text if the user does not have permissions', async () => {
    siteContactsService.getCaSiteContacts.mockReturnValueOnce(asyncData({ ...siteContacts, editable: false }));
    activatedRoute.setFragment('site-contacts');

    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.assignees.map((assignee) => assignee.textContent)).toEqual(['Unassigned', 'Bob Squarepants']);
  });

  it('should display only active regulators', async () => {
    activatedRoute.setFragment('site-contacts');

    await fixture.whenStable();
    fixture.detectChanges();

    expect(Array.from(page.assigneeSelects[0].options).map((option) => option.textContent.trim())).toEqual([
      'Unassigned',
      'Bob Squarepants',
      'Patrick Star',
    ]);
  });
});
