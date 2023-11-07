import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { CreateSummaryComponent } from '@aviation/request-task/vir/review/tasks/create-summary/create-summary.component';
import { CreateSummaryFormProvider } from '@aviation/request-task/vir/review/tasks/create-summary/create-summary-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

describe('CreateSummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let component: CreateSummaryComponent;
  let fixture: ComponentFixture<CreateSummaryComponent>;
  let formProvider: CreateSummaryFormProvider;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<CreateSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get reportSummary() {
      return this.getInputValue('#reportSummary');
    }

    set reportSummary(value: string) {
      this.setInputValue('#reportSummary', value);
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(CreateSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateSummaryComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: CreateSummaryFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new create summary details', () => {
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
              reviewSectionsCompleted: {},
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
                regulatorImprovementResponses: {},
              },
            },
          },
        },
      } as any);

      formProvider.setFormValue(null);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Create summary');
      expect(page.errorSummary).toBeFalsy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual(['Enter your summary response for the operator']);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to summary page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual(['Enter your summary response for the operator']);

      page.reportSummary = 'Test summary';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_VIR_SAVE_REVIEW',
        requestTaskActionPayload: {
          regulatorReviewResponse: {
            regulatorImprovementResponses: {},
            reportSummary: 'Test summary',
          },
          reviewSectionsCompleted: { createSummary: false },
          payloadType: 'AVIATION_VIR_SAVE_REVIEW_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing create summary details', () => {
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
                createSummary: true,
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
      } as any);
      formProvider.setFormValue(store.virDelegate.payload.regulatorReviewResponse.reportSummary);

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Create summary');
      expect(page.reportSummary).toEqual('Test summary');
      expect(page.errorSummary).toBeFalsy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to summary page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.reportSummary = 'Test summary, changed';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
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
            reportSummary: 'Test summary, changed',
          },
          reviewSectionsCompleted: {
            createSummary: false,
            B1: true,
          },
          payloadType: 'AVIATION_VIR_SAVE_REVIEW_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
