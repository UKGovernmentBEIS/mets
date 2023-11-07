import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthStore } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AviationAccountsStore } from '../../store';
import { mockedAccount } from '../../testing/mock-data';
import { ViewAviationAccountComponent } from './view-aviation-account.component';

describe('ViewAviationAccountComponent', () => {
  let component: ViewAviationAccountComponent;
  let fixture: ComponentFixture<ViewAviationAccountComponent>;
  let page: Page;
  let store: AviationAccountsStore;
  let authStore: AuthStore;

  class Page extends BasePage<ViewAviationAccountComponent> {
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
    await TestBed.configureTestingModule({
      declarations: [ViewAviationAccountComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [AviationAccountsStore],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(ViewAviationAccountComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(AviationAccountsStore);
    authStore = TestBed.inject(AuthStore);
    store.setCurrentAccount(mockedAccount);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the account name', () => {
    expect(page.heading.textContent.trim()).toContain(mockedAccount.aviationAccount.name);
  });

  it('should render the status', () => {
    expect(page.status.textContent.trim()).toEqual('New');
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

    it('should render all tabs except notes', () => {
      expect(page.tabs.map((tab) => tab.textContent.trim())).toEqual([
        'Details',
        'Emissions plan history',
        'Reports',
        'Users and contacts',
      ]);
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
        'Emissions plan history',
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

    it('should not render users and notes tab', () => {
      expect(page.tabs.map((tab) => tab.textContent.trim())).toEqual(['Details', 'Emissions plan history', 'Reports']);
    });
  });
});
