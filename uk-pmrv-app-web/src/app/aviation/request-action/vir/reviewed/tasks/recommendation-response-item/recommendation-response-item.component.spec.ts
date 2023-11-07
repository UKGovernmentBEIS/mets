import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { RequestActionStore } from '@aviation/request-action/store';
import { RecommendationResponseItemComponent } from '@aviation/request-action/vir/reviewed/tasks/recommendation-response-item/recommendation-response-item.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { AviationVirApplicationReviewedRequestActionPayload, UncorrectedItem } from 'pmrv-api';

class Page extends BasePage<RecommendationResponseItemComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('RecommendationResponseItemComponent', () => {
  let component: RecommendationResponseItemComponent;
  let fixture: ComponentFixture<RecommendationResponseItemComponent>;
  let store: RequestActionStore;
  let page: Page;

  const currentItem = {
    reference: 'B1',
    explanation: 'Test uncorrectedNonConformity',
    materialEffect: true,
  } as UncorrectedItem;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 19, id: currentItem.reference }, null, {
    verificationDataItem: currentItem,
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, RecommendationResponseItemComponent],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
        { provide: ActivatedRoute, useValue: activatedRoute },
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
              B1: currentItem,
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

    fixture = TestBed.createComponent(RecommendationResponseItemComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('B1: an uncorrected error in the monitoring plan');
    expect(page.summaryValues).toEqual([
      ['Item code', 'B1: an uncorrected error in the monitoring plan'],
      ["Verifier's recommendation", 'Test uncorrectedNonConformity'],
      ['Material?', 'Yes'],
      ['Addressed?', 'No'],
      ['Tell us why you have chosen not to address this recommendation', 'Test description B1, when no'],
      ['Evidence uploaded?', 'No'],
      ["Regulator's decision", 'Improvement is required'],
      ["Regulator's comments", 'Test improvement comments B1'],
      ['Actions for the operator', 'Test operator actions B1'],
      ['Deadline for improvement', '1 Dec 2023'],
    ]);
  });
});
