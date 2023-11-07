import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { RecommendationResponseItemFormProvider } from '@aviation/request-task/vir/review/tasks/recommendation-response-item/recommendation-response-item-form.provider';
import { SummaryComponent } from '@aviation/request-task/vir/review/tasks/recommendation-response-item/summary/summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RegulatorImprovementResponse, TasksService, UncorrectedItem } from 'pmrv-api';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let formProvider: RecommendationResponseItemFormProvider;

  const currentItem = {
    reference: 'B1',
    explanation: 'Test uncorrectedNonConformity',
    materialEffect: true,
  } as UncorrectedItem;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 19, id: currentItem.reference }, null, {
    verificationDataItem: currentItem,
  });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SummaryComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: RecommendationResponseItemFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<RecommendationResponseItemFormProvider>(TASK_FORM_PROVIDER);

    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'AVIATION_VIR_APPLICATION_REVIEW',
          payload: {
            payloadType: 'AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD',
            verificationData: {
              uncorrectedNonConformities: {
                B1: currentItem,
              },
            },
            reviewSectionsCompleted: { B1: false },
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
            virAttachments: {},
          },
        },
      },
      isEditable: true,
    } as any);
    formProvider.setFormValue(
      store.virDelegate.payload.regulatorReviewResponse.regulatorImprovementResponses[
        'B1'
      ] as RegulatorImprovementResponse,
    );

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
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

  it('should submit a valid form and navigate to `upload-evidence-files` page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 19,
      requestTaskActionType: 'AVIATION_VIR_SAVE_REVIEW',
      requestTaskActionPayload: {
        regulatorReviewResponse: {
          regulatorImprovementResponses: {
            B1: {
              improvementRequired: true,
              improvementDeadline: new Date('2023-12-01'),
              improvementComments: 'Test improvement comments B1',
              operatorActions: 'Test operator actions B1',
            },
          },
          reportSummary: 'Test summary',
        },
        reviewSectionsCompleted: { B1: true },
        payloadType: 'AVIATION_VIR_SAVE_REVIEW_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../../../..'], { relativeTo: activatedRoute });
  });
});
