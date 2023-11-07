import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemFormProvider } from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item-form.provider';
import { UploadEvidenceQuestionComponent } from '@aviation/request-task/vir/submit/tasks/reference-item/upload-evidence-question/upload-evidence-question.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { OperatorImprovementResponse, TasksService, UncorrectedItem } from 'pmrv-api';

describe('UploadEvidenceQuestionComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: UploadEvidenceQuestionComponent;
  let fixture: ComponentFixture<UploadEvidenceQuestionComponent>;
  let formProvider: ReferenceItemFormProvider;

  const currentItem = {
    reference: 'B1',
    explanation: 'Test uncorrectedNonConformity',
    materialEffect: true,
  } as UncorrectedItem;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 19, id: currentItem.reference }, null, {
    verificationDataItem: currentItem,
  });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<UploadEvidenceQuestionComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get uploadEvidenceButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="uploadEvidence"]');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(UploadEvidenceQuestionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadEvidenceQuestionComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ReferenceItemFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new upload evidence question details', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<ReferenceItemFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestTask: {
            id: 19,
            type: 'AVIATION_VIR_APPLICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
              verificationData: {
                uncorrectedNonConformities: {
                  B1: currentItem,
                },
              },
              virSectionsCompleted: { B1: false },
              operatorImprovementResponses: {
                B1: {
                  isAddressed: false,
                  addressedDescription: 'Test description B1, when no',
                  files: [],
                },
              },
            },
          },
        },
      } as any);
      formProvider.setFormValue(
        store.virDelegate.payload.operatorImprovementResponses['B1'] as OperatorImprovementResponse,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Would you like to upload evidence to support your response?');

      expect(page.uploadEvidenceButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual(['Select yes or no']);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to `upload-evidence-files` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.uploadEvidenceButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_VIR_SAVE_APPLICATION',
        requestTaskActionPayload: {
          operatorImprovementResponses: {
            B1: {
              isAddressed: false,
              addressedDescription: 'Test description B1, when no',
              addressedDate: null,
              uploadEvidence: true,
              files: [],
            },
          },
          virSectionsCompleted: { B1: false },
          payloadType: 'AVIATION_VIR_SAVE_APPLICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['../upload-evidence-files'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing upload evidence question details', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<ReferenceItemFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestTask: {
            id: 19,
            type: 'AVIATION_VIR_APPLICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
              verificationData: {
                uncorrectedNonConformities: {
                  B1: currentItem,
                },
              },
              virSectionsCompleted: { B1: false },
              operatorImprovementResponses: {
                B1: {
                  isAddressed: false,
                  addressedDescription: 'Test description B1, when no',
                  uploadEvidence: true,
                  files: ['uuid'],
                },
              },
              virAttachments: { uuid: 'test-file.png' },
            },
          },
        },
      } as any);
      formProvider.setFormValue(
        store.virDelegate.payload.operatorImprovementResponses['B1'] as OperatorImprovementResponse,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Would you like to upload evidence to support your response?');

      expect(page.uploadEvidenceButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.uploadEvidenceButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_VIR_SAVE_APPLICATION',
        requestTaskActionPayload: {
          operatorImprovementResponses: {
            B1: {
              isAddressed: false,
              addressedDescription: 'Test description B1, when no',
              addressedDate: null,
              uploadEvidence: false,
              files: [],
            },
          },
          virSectionsCompleted: { B1: false },
          payloadType: 'AVIATION_VIR_SAVE_APPLICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });
  });
});
