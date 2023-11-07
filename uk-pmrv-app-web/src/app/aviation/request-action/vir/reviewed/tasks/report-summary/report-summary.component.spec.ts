import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { RequestActionStore } from '@aviation/request-action/store';
import { ReportSummaryComponent } from '@aviation/request-action/vir/reviewed/tasks/report-summary/report-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { AviationVirApplicationReviewedRequestActionPayload } from 'pmrv-api';

class Page extends BasePage<ReportSummaryComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('ReportSummaryComponent', () => {
  let component: ReportSummaryComponent;
  let fixture: ComponentFixture<ReportSummaryComponent>;
  let store: RequestActionStore;
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, ReportSummaryComponent],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_VIR_APPLICATION_REVIEWED',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          payloadType: 'AVIATION_VIR_APPLICATION_REVIEWED_PAYLOAD',
          reportingYear: 2022,
          verificationData: {
            uncorrectedNonConformities: {
              B1: {
                reference: 'B1',
                explanation: 'Test uncorrectedNonConformity',
                materialEffect: true,
              },
            },
          },
          operatorImprovementResponses: {
            B1: {
              isAddressed: false,
              addressedDescription: 'Test description B1, when no',
              uploadEvidence: false,
              files: [],
            },
          },
          regulatorReviewResponse: {
            regulatorImprovementResponses: {
              B1: {
                improvementRequired: true,
                improvementDeadline: '2023-12-01',
                improvementComments: 'Test improvement comments B1',
                operatorActions: 'Test operator actions B1',
              },
            },
            reportSummary: 'Test summary',
          },
          decisionNotification: {
            operators: ['op1', 'op2'],
            signatory: 'reg',
          },
          officialNotice: {},
        } as AviationVirApplicationReviewedRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(ReportSummaryComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Create summary');
    expect(page.summaryValues).toEqual([['Report summary', 'Test summary']]);
  });
});
