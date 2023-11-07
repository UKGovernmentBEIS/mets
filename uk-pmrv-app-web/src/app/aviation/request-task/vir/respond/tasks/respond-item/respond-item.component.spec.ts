import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { RespondItemComponent } from '@aviation/request-task/vir/respond/tasks/respond-item/respond-item.component';
import { RespondItemFormProvider } from '@aviation/request-task/vir/respond/tasks/respond-item/respond-item-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { OperatorImprovementFollowUpResponse, TasksService, UncorrectedItem } from 'pmrv-api';

describe('RespondItemComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: RespondItemComponent;
  let fixture: ComponentFixture<RespondItemComponent>;
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
  const expectedDate = new Date('2023-12-01');

  class Page extends BasePage<RespondItemComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get verificationItem() {
      return this.query('app-verification-recommendation-item');
    }

    get operatorResponseItem() {
      return this.query('app-operator-response-item');
    }

    get regulatorResponseItem() {
      return this.query('app-regulator-response-item');
    }

    get improvementCompletedButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="improvementCompleted"]');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    set dateCompleted(date: Date) {
      this.setInputValue(`#dateCompleted-day`, date.getDate());
      this.setInputValue(`#dateCompleted-month`, date.getMonth() + 1);
      this.setInputValue(`#dateCompleted-year`, date.getFullYear());
    }

    set reason(value: string) {
      this.setInputValue('#reason', value);
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(RespondItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RespondItemComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: RespondItemFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new respond details', () => {
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
              virRespondToRegulatorCommentsSectionsCompleted: {},
              operatorImprovementResponses: {
                B1: {
                  isAddressed: false,
                  addressedDescription: 'Not addressed',
                  addressedDate: null,
                  uploadEvidence: false,
                  files: [],
                },
              },
              regulatorImprovementResponses: {
                B1: {
                  operatorActions: 'Actions B1',
                  improvementDeadline: '2023-12-01',
                  improvementRequired: true,
                },
              },
              operatorImprovementFollowUpResponses: {},
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
      expect(page.heading1.textContent.trim()).toEqual(`Respond to B1`);
      expect(page.verificationItem).toBeTruthy();
      expect(page.operatorResponseItem).toBeTruthy();
      expect(page.regulatorResponseItem).toBeTruthy();
      expect(page.improvementCompletedButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual(['Select if the required improvement is complete']);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to summary page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.improvementCompletedButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual(['Enter a date']);

      page.improvementCompletedButtons[0].click();
      page.dateCompleted = expectedDate;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS',
        requestTaskActionPayload: {
          reference: 'B1',
          virRespondToRegulatorCommentsSectionsCompleted: { B1: false },
          operatorImprovementFollowUpResponse: {
            improvementCompleted: true,
            dateCompleted: expectedDate,
          },
          payloadType: 'AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing respond details', () => {
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
              virRespondToRegulatorCommentsSectionsCompleted: {},
              operatorImprovementResponses: {
                B1: {
                  isAddressed: false,
                  addressedDescription: 'Not addressed',
                  addressedDate: null,
                  uploadEvidence: false,
                  files: [],
                },
              },
              regulatorImprovementResponses: {
                B1: {
                  operatorActions: 'Actions B1',
                  improvementDeadline: '2023-12-01',
                  improvementRequired: true,
                },
              },
              operatorImprovementFollowUpResponses: {
                B1: {
                  improvementCompleted: true,
                  dateCompleted: '2022-06-03',
                },
              },
            },
          },
        },
      } as any);

      formProvider.setFormValue(
        store.virDelegate.payload.operatorImprovementFollowUpResponses['B1'] as OperatorImprovementFollowUpResponse,
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
      expect(page.improvementCompletedButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to summary page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.improvementCompletedButtons[1].click();
      page.reason = 'Test description B1, when no';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS',
        requestTaskActionPayload: {
          reference: 'B1',
          virRespondToRegulatorCommentsSectionsCompleted: { B1: false },
          operatorImprovementFollowUpResponse: {
            improvementCompleted: false,
            reason: 'Test description B1, when no',
          },
          payloadType: 'AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
