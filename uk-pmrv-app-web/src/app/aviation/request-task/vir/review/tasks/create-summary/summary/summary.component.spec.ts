import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { CreateSummaryFormProvider } from '@aviation/request-task/vir/review/tasks/create-summary/create-summary-form.provider';
import { SummaryComponent } from '@aviation/request-task/vir/review/tasks/create-summary/summary/summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let formProvider: CreateSummaryFormProvider;

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
        { provide: TASK_FORM_PROVIDER, useClass: CreateSummaryFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<CreateSummaryFormProvider>(TASK_FORM_PROVIDER);

    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'AVIATION_VIR_APPLICATION_REVIEW',
          payload: {
            payloadType: 'AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD',
            verificationData: {
              uncorrectedNonConformities: {
                B1: {
                  reference: 'B1',
                  explanation: 'Test uncorrectedNonConformity',
                  materialEffect: true,
                },
              },
            },
            reviewSectionsCompleted: {
              createSummary: false,
              B1: true,
            },
            operatorImprovementResponses: {
              B1: {
                isAddressed: false,
                addressedDescription: 'Not addressed',
                addressedDate: null,
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
          },
        },
      },
      isEditable: true,
    } as any);
    formProvider.setFormValue(store.virDelegate.payload.regulatorReviewResponse.reportSummary);

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.summaryValues).toEqual([['Report summary', 'Test summary']]);
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
              improvementDeadline: '2023-12-01',
              improvementComments: 'Test improvement comments B1',
              operatorActions: 'Test operator actions B1',
            },
          },
          reportSummary: 'Test summary',
        },
        reviewSectionsCompleted: {
          createSummary: true,
          B1: true,
        },
        payloadType: 'AVIATION_VIR_SAVE_REVIEW_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../../../..'], { relativeTo: activatedRoute });
  });
});
