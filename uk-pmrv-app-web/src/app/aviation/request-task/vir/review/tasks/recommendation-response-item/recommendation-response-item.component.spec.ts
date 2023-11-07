import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { RecommendationResponseItemComponent } from '@aviation/request-task/vir/review/tasks/recommendation-response-item/recommendation-response-item.component';
import { RecommendationResponseItemFormProvider } from '@aviation/request-task/vir/review/tasks/recommendation-response-item/recommendation-response-item-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RegulatorImprovementResponse, TasksService, UncorrectedItem } from 'pmrv-api';

describe('RecommendationResponseItemComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: RecommendationResponseItemComponent;
  let fixture: ComponentFixture<RecommendationResponseItemComponent>;
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
  const expectedDate = new Date('2023-12-01');

  class Page extends BasePage<RecommendationResponseItemComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get verificationItem() {
      return this.query('app-verification-recommendation-item');
    }

    get operatorResponseItem() {
      return this.query('app-operator-response-item');
    }

    get improvementRequiredButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="improvementRequired"]');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get improvementComments() {
      return this.getInputValue('#improvementComments');
    }

    set improvementComments(value: string) {
      this.setInputValue('#improvementComments', value);
    }

    get operatorActions() {
      return this.getInputValue('#operatorActions');
    }

    set operatorActions(value: string) {
      this.setInputValue('#operatorActions', value);
    }

    set improvementDeadline(date: Date) {
      this.setInputValue(`#improvementDeadline-day`, date.getDate());
      this.setInputValue(`#improvementDeadline-month`, date.getMonth() + 1);
      this.setInputValue(`#improvementDeadline-year`, date.getFullYear());
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(RecommendationResponseItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecommendationResponseItemComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: RecommendationResponseItemFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new recommendation response details', () => {
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
      expect(page.heading1.textContent.trim()).toEqual('Respond to B1');
      expect(page.verificationItem).toBeTruthy();
      expect(page.operatorResponseItem).toBeTruthy();
      expect(page.improvementRequiredButtons).toHaveLength(2);
      expect(page.improvementComments).toEqual('');
      expect(page.operatorActions).toEqual('');
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([
        'Select if the improvement is required',
        'Enter your actions for the operator',
      ]);
      expect(page.errorSummaryListContents.length).toEqual(2);
    });

    it('should submit a valid form and navigate to summary page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.improvementRequiredButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual(['Enter a date', 'Enter your actions for the operator']);

      page.improvementRequiredButtons[0].click();
      page.improvementDeadline = expectedDate;
      page.improvementComments = 'Test improvement comments B1';
      page.operatorActions = 'Test operator actions B1';
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
                improvementDeadline: expectedDate,
                improvementComments: 'Test improvement comments B1',
                operatorActions: 'Test operator actions B1',
              },
            },
          },
          reviewSectionsCompleted: { B1: false },
          payloadType: 'AVIATION_VIR_SAVE_REVIEW_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing recommendation response details', () => {
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
      formProvider.setFormValue(
        store.virDelegate.payload.regulatorReviewResponse.regulatorImprovementResponses[
          'B1'
        ] as RegulatorImprovementResponse,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Respond to B1');
      expect(page.verificationItem).toBeTruthy();
      expect(page.operatorResponseItem).toBeTruthy();
      expect(page.improvementRequiredButtons).toHaveLength(2);
      expect(page.improvementComments).toEqual('Test improvement comments B1');
      expect(page.operatorActions).toEqual('Test operator actions B1');
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to summary page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.improvementRequiredButtons[1].click();
      page.improvementComments = 'Test description B1, changed';
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
                improvementRequired: false,
                improvementDeadline: null,
                improvementComments: 'Test description B1, changed',
                operatorActions: 'Test operator actions B1',
              },
            },
            reportSummary: 'Test summary',
          },
          reviewSectionsCompleted: { B1: false },
          payloadType: 'AVIATION_VIR_SAVE_REVIEW_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
