import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthStore } from '@core/store/auth';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockState } from '@permit-application/testing/mock-state';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { PermitIssuanceStore } from '../../store/permit-issuance.store';
import { DecisionSummaryComponent } from './decision-summary.component';

describe('DecisionSummaryComponent', () => {
  let component: DecisionSummaryComponent;
  let fixture: ComponentFixture<DecisionSummaryComponent>;
  let store: PermitIssuanceStore;
  let authStore: AuthStore;
  let page: Page;

  const govukDate = new GovukDatePipe();
  const currentDate = new Date().toISOString();
  const currentDateText = govukDate.transform(currentDate, 'datetime');

  class Page extends BasePage<DecisionSummaryComponent> {
    get heading() {
      return `${this.query<HTMLHeadingElement>(
        'app-request-action-heading h1',
      ).textContent.trim()} ${this.query<HTMLHeadingElement>('app-request-action-heading p').textContent.trim()}`;
    }
    get titles() {
      return this.queryAll<HTMLDListElement>('dl').map((el) =>
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
      );
    }
    get values() {
      return this.queryAll<HTMLDListElement>('dl').map((el) =>
        Array.from(el.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(DecisionSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
      declarations: [DecisionSummaryComponent],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });
  });

  describe('for granted permit application', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockState,
        determination: {
          activationDate: '2022-02-22 14:26:44',
          reason: 'Grant reason',
          type: 'GRANTED',
        },
        decisionNotification: {
          operators: ['op1', 'op2'],
          signatory: 'reg',
        },
        usersInfo: {
          op1: {
            contactTypes: ['PRIMARY'],
            name: 'Operator1 Name',
          },
          op2: {
            contactTypes: ['PRIMARY'],
            name: 'Operator2 Name',
          },
          reg: {
            name: 'Regulator Name',
          },
        },
        requestActionCreationDate: currentDate,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the details', () => {
      expect(page.heading).toEqual(`Permit application approved ${currentDateText}`);
      expect(page.titles).toEqual([
        ['Permit application', 'Decision', 'Reason for decision', 'Permit effective date'],
        ['Users', 'Name and signature on the official notice'],
      ]);
      expect(page.values).toEqual([
        ['Permit application', 'Grant', 'Grant reason', '22 Feb 2022'],
        ['Operator1 Name - Primary contact  Operator2 Name - Primary contact', 'Regulator Name'],
      ]);
    });
  });

  describe('for rejected permit application', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockState,
        determination: {
          reason: 'Reject reason',
          type: 'REJECTED',
        },
        decisionNotification: {
          operators: ['op1', 'op2'],
          signatory: 'reg',
        },
        usersInfo: {
          op1: {
            contactTypes: ['PRIMARY'],
            name: 'Operator1 Name',
          },
          op2: {
            contactTypes: ['PRIMARY'],
            name: 'Operator2 Name',
          },
          reg: {
            name: 'Regulator Name',
          },
        },
        requestActionCreationDate: currentDate,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the details', () => {
      expect(page.heading).toEqual(`Permit application rejected ${currentDateText}`);
      expect(page.titles).toEqual([
        ['Decision', 'Reason for decision'],
        ['Users', 'Name and signature on the official notice'],
      ]);
      expect(page.values).toEqual([
        ['Reject', 'Reject reason'],
        ['Operator1 Name - Primary contact  Operator2 Name - Primary contact', 'Regulator Name'],
      ]);
    });
  });

  describe('for deemed withdrawn permit application', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockState,
        determination: {
          reason: 'Deemed withdrawn reason',
          type: 'DEEMED_WITHDRAWN',
        },
        decisionNotification: {
          operators: ['op1', 'op2'],
          signatory: 'reg',
        },
        usersInfo: {
          op1: {
            contactTypes: ['PRIMARY'],
            name: 'Operator1 Name',
          },
          op2: {
            contactTypes: ['PRIMARY'],
            name: 'Operator2 Name',
          },
          reg: {
            name: 'Regulator Name',
          },
        },
        requestActionCreationDate: currentDate,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the details', () => {
      expect(page.heading).toEqual(`Permit application deemed withdrawn ${currentDateText}`);
      expect(page.titles).toEqual([
        ['Decision', 'Reason for decision'],
        ['Users', 'Name and signature on the official notice'],
      ]);
      expect(page.values).toEqual([
        ['Deem withdrawn', 'Deemed withdrawn reason'],
        ['Operator1 Name - Primary contact  Operator2 Name - Primary contact', 'Regulator Name'],
      ]);
    });
  });
});
