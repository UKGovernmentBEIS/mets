import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { RequestActionStore } from '@aviation/request-action/store';
import { DecisionSummaryComponent } from '@aviation/request-action/vir/decision-summary/decision-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

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
        type: 'AVIATION_VIR_APPLICATION_REVIEWED',
        creationDate: currentDate,
        payload: {
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
          officialNotice: {
            name: 'Recommended_improvements.pdf',
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
    expect(page.heading).toEqual(`Verifier improvement report decision submitted ${currentDateText}`);
    expect(page.titles).toEqual([
      ['Verifier improvement report'],
      ['Users', 'Name and signature on the official notice', 'Official notice'],
    ]);
    expect(page.values).toEqual([
      ['Verifier improvement report'],
      [
        'Operator1 Name - Primary contact  Operator2 Name - Primary contact',
        'Regulator Name',
        'Recommended_improvements.pdf',
      ],
    ]);
  });
});
