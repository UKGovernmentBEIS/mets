import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { DecisionSummaryComponent } from './decision-summary.component';

describe('DecisionSummaryComponent', () => {
  let component: DecisionSummaryComponent;
  let fixture: ComponentFixture<DecisionSummaryComponent>;
  let store: RequestActionStore;
  let page: Page;

  const route = new ActivatedRouteStub();
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionSummaryComponent, SharedModule, RequestActionTaskComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED',
        creationDate: currentDate,
        payload: {
          determination: {
            type: 'APPROVED',
            reason: 'reason',
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
          decisionNotification: {
            operators: ['op1', 'op2'],
            signatory: 'reg',
          },
          empDocument: {
            name: 'UK-E-AV-00081 v1.pdf',
            uuid: 'cafdd4c4-a010-4251-a105-3c489cee8590',
          },
          officialNotice: {
            name: 'EMP_application_approved.pdf',
            uuid: 'b7245908-0f9c-4562-8f3a-177ef5d1503b',
          },
        } as any,
      },
      regulatorViewer: true,
    });

    fixture = TestBed.createComponent(DecisionSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the details', () => {
    expect(page.heading).toEqual(`Approved ${currentDateText}`);
    expect(page.titles).toEqual([
      ['Emissions plan', 'Emissions plan application'],
      ['Decision', 'Reason for decision'],
      ['Users', 'Name and signature on the official notice', 'Official notice'],
    ]);
    expect(page.values).toEqual([
      ['UK-E-AV-00081 v1.pdf', 'Emissions plan application'],
      ['Approve', 'reason'],
      [
        'Operator1 Name - Primary contact  Operator2 Name - Primary contact',
        'Regulator Name',
        'EMP_application_approved.pdf',
      ],
    ]);
  });
});
