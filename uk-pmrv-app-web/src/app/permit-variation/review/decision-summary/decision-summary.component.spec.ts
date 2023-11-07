import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthStore } from '@core/store/auth';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { PermitVariationStore } from '../../store/permit-variation.store';
import { mockPermitVariationRegulatorLedPayload } from '../../testing/mock';
import { DecisionSummaryComponent } from './decision-summary.component';

describe('DecisionSummaryComponent', () => {
  let component: DecisionSummaryComponent;
  let fixture: ComponentFixture<DecisionSummaryComponent>;
  let store: PermitVariationStore;
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
          useExisting: PermitVariationStore,
        },
      ],
      declarations: [DecisionSummaryComponent],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });
  });

  describe('for permit variation regulator led application', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
        determination: {
          reason: 'reason',
          activationDate: '2023-01-01',
          reasonTemplate: 'FOLLOWING_IMPROVEMENT_REPORT_BY_OPERATOR',
          logChanges: 'logChanges',
        },
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: {
            notes: 'kkj',
            variationScheduleItems: ['item1', 'item2'],
          },
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
      expect(page.heading).toEqual(`Permit variation approved ${currentDateText}`);
      expect(page.titles).toEqual([
        [
          'Permit variation',
          'Decision',
          'Reason for decision',
          'Permit activation date',
          'Reason template for the official notice',
          'Items added to the variation log',
          'Items added to the variation schedule',
        ],
        ['Users', 'Name and signature on the official notice'],
      ]);
      expect(page.values).toEqual([
        [
          'Permit variation application',
          'Approve',
          'reason',
          '1 Jan 2023',
          'Following an improvement report submitted by an operator',
          'logChanges',
          '1. item1 2. item2',
        ],
        ['Operator1 Name - Primary contact  Operator2 Name - Primary contact', 'Regulator Name'],
      ]);
    });
  });
});
