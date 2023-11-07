import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { SendReportComponent } from '@aviation/request-task/vir/submit/tasks/send-report/send-report.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

describe('SendReportComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: SendReportComponent;
  let fixture: ComponentFixture<SendReportComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendReportComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get heading3(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h3');
    }

    get paragraph(): HTMLParagraphElement {
      return this.query<HTMLParagraphElement>('p.govuk-body');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }

    get panelBody() {
      return this.query<HTMLElement>('div.govuk-panel__body');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SendReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SendReportComponent, RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('when it can be submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);

      store.setState({
        requestTaskItem: {
          requestInfo: {
            id: 'VIR00003-2023',
          },
          requestTask: {
            id: 19,
            type: 'AVIATION_VIR_APPLICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
              verificationData: {
                uncorrectedNonConformities: {
                  B1: {
                    reference: 'B1',
                    explanation: 'Test uncorrectedNonConformity',
                    materialEffect: true,
                  },
                },
              },
              virSectionsCompleted: { B1: true },
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
        isEditable: true,
      } as any);

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Submit responses');
      expect(page.paragraph.textContent.trim()).toEqual(
        'Are you sure you want to submit this form to your regulator for assessment?',
      );
      expect(page.submitButton.textContent.trim()).toEqual('Confirm and complete');
    });

    it('should submit and show success panel', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'AVIATION_VIR_SUBMIT_APPLICATION',
        requestTaskId: 19,
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        },
      });

      expect(page.heading1.textContent.trim()).toEqual('Responses submitted');
      expect(page.panelBody.textContent.trim()).toEqual(
        'Your responses have been successfully submitted to your regulator for assessmentYour reference code is: VIR00003-2023',
      );
      expect(page.heading3.textContent.trim()).toEqual('What happens next');
      expect(page.paragraph.textContent.trim()).toEqual(
        'We will review what you have told us and will reply to you in due course.',
      );
      expect(page.submitButton).toBeFalsy();
    });
  });

  describe('when it cannot be submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);

      store.setState({
        requestTaskItem: {
          requestInfo: {
            id: 'VIR00003-2023',
          },
          requestTask: {
            id: 19,
            type: 'AVIATION_VIR_APPLICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
              verificationData: {
                uncorrectedNonConformities: {
                  B1: {
                    reference: 'B1',
                    explanation: 'Test uncorrectedNonConformity',
                    materialEffect: true,
                  },
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

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Submit responses');
      expect(page.paragraph.textContent.trim()).toEqual('You need to complete all tasks before submitting the report.');
      expect(page.submitButton).toBeFalsy();
    });
  });
});
