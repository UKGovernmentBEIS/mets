import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { mockAccountContactVbInfoResponse, mockVerifiersRouteData } from '../testing/mock-data';
import { SiteContactsComponent } from './site-contacts.component';

describe('SiteContactsComponent', () => {
  let hostComponent: TestComponent;
  let component: SiteContactsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let activatedRoute: ActivatedRouteStub;
  let router: Router;

  class Page extends BasePage<TestComponent> {
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

  @Component({
    standalone: false,
    template: `
      <app-verifier-site-contacts
        [pageSize]="pageSize"
        [contacts]="contacts"
        [verifiers]="verifiers"
        (siteContactChange)="siteContactChange($any($event))"></app-verifier-site-contacts>
    `,
  })
  class TestComponent {
    contacts = mockAccountContactVbInfoResponse;
    verifiers = mockVerifiersRouteData.verifiers.authorities;
    pageSize = 50;
    siteContactChange = jest.fn();
  }

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub();

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [SiteContactsComponent, TestComponent],
      providers: [{ provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();
  });

  beforeEach(async () => {
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(SiteContactsComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of accounts with their assignees', () => {
    expect(page.accounts.map((header) => header.textContent)).toEqual(['Account 1', 'Account 2', 'Account 3']);
    expect(page.assigneeSelectValues).toEqual(['2reg', null, null]);
    expect(page.assigneeSelects.map((select) => select.selectedOptions[0].textContent.trim())).toEqual([
      'Therion Path',
      'Unassigned',
      'Unassigned',
    ]);
  });

  it('should not show a Type column', () => {
    expect(page.query('thead').textContent).not.toContain('Type');
  });

  it('should submit the updated assignees', () => {
    page.assigneeSelectValues = [null, '4reg', null];
    fixture.detectChanges();

    page.saveButton.click();
    fixture.detectChanges();

    expect(hostComponent.siteContactChange).toHaveBeenCalledWith([
      { accountId: 1, userId: null },
      { accountId: 2, userId: '4reg' },
      { accountId: 3, userId: null },
    ]);
  });

  it('should display assignees as plain text if the user is not verifier admin', () => {
    const contacts = { ...mockAccountContactVbInfoResponse };
    contacts.editable = false;
    hostComponent.contacts = contacts;
    fixture.detectChanges();

    expect(page.assignees.map((assignee) => assignee.textContent)).toEqual([
      'Therion Path',
      'Unassigned',
      'Unassigned',
    ]);
  });

  it('should display only active regulators', () => {
    expect(Array.from(page.assigneeSelects[0].options).map((option) => option.textContent.trim())).toEqual([
      'Unassigned',
      'Therion Path',
      'Tyrion Lanister',
    ]);
  });

  it('should show an inline error and not navigate when the term is fewer than 3 characters', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.termValue = 'te';
    page.searchButton.click();
    fixture.detectChanges();

    expect(page.termErrorMessage.textContent).toContain('Enter at least 3 characters');
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should navigate with the term and reset the page on search', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.termValue = 'account';
    page.searchButton.click();
    fixture.detectChanges();

    expect(page.termErrorMessage).toBeNull();
    expect(navigateSpy).toHaveBeenCalledWith([], {
      relativeTo: activatedRoute,
      preserveFragment: true,
      queryParams: { term: 'account', page: null },
      queryParamsHandling: 'merge',
    });
  });

  it('should sync the search field from the URL', () => {
    activatedRoute.setQueryParamMap({ term: 'account' });
    fixture.detectChanges();

    expect(page.getInputValue('#term')).toEqual('account');
  });

  it('should show a no-results message when there are no contacts', () => {
    hostComponent.contacts = { ...mockAccountContactVbInfoResponse, contacts: [] };
    fixture.detectChanges();

    expect(page.noResultsMessage.textContent).toContain(
      'No matching accounts found. Try searching for a different account name, or clear the search to view all accounts.',
    );
    expect(page.accounts).toEqual([]);
  });
});
