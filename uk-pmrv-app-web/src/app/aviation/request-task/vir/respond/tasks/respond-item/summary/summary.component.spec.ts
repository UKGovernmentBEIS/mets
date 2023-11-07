import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { RespondItemFormProvider } from '@aviation/request-task/vir/respond/tasks/respond-item/respond-item-form.provider';
import { SummaryComponent } from '@aviation/request-task/vir/respond/tasks/respond-item/summary/summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { OperatorImprovementFollowUpResponse, TasksService, UncorrectedItem } from 'pmrv-api';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let formProvider: RespondItemFormProvider;

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
        { provide: TASK_FORM_PROVIDER, useClass: RespondItemFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<RespondItemFormProvider>(TASK_FORM_PROVIDER);

    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS',
          payload: {
            payloadType: 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
            verificationData: {
              uncorrectedNonConformities: {
                B1: currentItem,
              },
            },
            virRespondToRegulatorCommentsSectionsCompleted: { B1: false },
            operatorImprovementResponses: {
              B1: {
                isAddressed: false,
                addressedDescription: 'Test description B1, when no',
                uploadEvidence: false,
                files: [],
              },
            },
            regulatorImprovementResponses: {
              B1: {
                improvementRequired: true,
                improvementDeadline: '2023-12-01',
                improvementComments: 'Test improvement comments B1',
                operatorActions: 'Test operator actions B1',
              },
            },
            operatorImprovementFollowUpResponses: {
              B1: {
                improvementCompleted: false,
                reason: 'No improvement reason',
              },
            },
            virAttachments: {},
          },
        },
      },
      isEditable: true,
    } as any);
    formProvider.setFormValue(
      store.virDelegate.payload.operatorImprovementFollowUpResponses['B1'] as OperatorImprovementFollowUpResponse,
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
      ['Improvement complete?', 'No'],
      ['Reason for not addressing the recommendation', 'No improvement reason'],
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
      requestTaskActionType: 'AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS',
      requestTaskActionPayload: {
        reference: 'B1',
        virRespondToRegulatorCommentsSectionsCompleted: { B1: true },
        operatorImprovementFollowUpResponse: {
          improvementCompleted: false,
          reason: 'No improvement reason',
        },
        payloadType: 'AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../../../..'], { relativeTo: activatedRoute });
  });
});
