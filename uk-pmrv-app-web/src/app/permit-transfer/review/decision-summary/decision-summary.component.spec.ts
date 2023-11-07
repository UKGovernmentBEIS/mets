import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthStore } from '@core/store/auth';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockState } from '@permit-application/testing/mock-state';
import { PermitTransferStore } from '@permit-transfer/store/permit-transfer.store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { TransferDecisionSummaryComponent } from './decision-summary.component';

describe('TransferDecisionSummaryComponent', () => {
  let component: TransferDecisionSummaryComponent;
  let fixture: ComponentFixture<TransferDecisionSummaryComponent>;
  let store: PermitTransferStore;
  let authStore: AuthStore;
  let page: Page;

  const govukDate = new GovukDatePipe();
  const currentDate = new Date().toISOString();
  const currentDateText = govukDate.transform(currentDate, 'datetime');

  class Page extends BasePage<TransferDecisionSummaryComponent> {
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
    fixture = TestBed.createComponent(TransferDecisionSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TransferDecisionSummaryComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitTransferStore,
        },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });
  });

  describe('for granted permit transfer', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitTransferStore);
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
        requestActionType: 'PERMIT_TRANSFER_B_APPLICATION_GRANTED',
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the details', () => {
      expect(page.heading).toEqual(`Permit transfer approved ${currentDateText}`);
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

  describe('for rejected permit transfer', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitTransferStore);
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
        requestActionType: 'PERMIT_TRANSFER_B_APPLICATION_REJECTED',
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the details', () => {
      expect(page.heading).toEqual(`Permit transfer rejected ${currentDateText}`);
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

  describe('for deemed withdrawn permit transfer', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitTransferStore);
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
        requestActionType: 'PERMIT_TRANSFER_B_APPLICATION_DEEMED_WITHDRAWN',
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the details', () => {
      expect(page.heading).toEqual(`Permit transfer deemed withdrawn ${currentDateText}`);
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
