import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { SendReportComponent } from '@aviation/request-task/vir/review/tasks/send-report/send-report.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

describe('SendReportComponent', () => {
  let page: Page;
  let component: SendReportComponent;
  let store: RequestTaskStore;
  let fixture: ComponentFixture<SendReportComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendReportComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get notifyOperator(): HTMLElement {
      return this.query<HTMLElement>('app-notify-operator');
    }

    get paragraph(): HTMLParagraphElement {
      return this.query<HTMLParagraphElement>('p.govuk-body');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SendReportComponent, RouterTestingModule],
      providers: [
        DestroySubject,
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
            accountId: 1,
          },
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
              reviewSectionsCompleted: { B1: true, createSummary: true },
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

      fixture = TestBed.createComponent(SendReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.notifyOperator).toBeTruthy();
    });
  });

  describe('when it cannot be submitted', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      store.setState({
        requestTaskItem: {
          requestInfo: {
            id: 'VIR00003-2023',
            accountId: 1,
          },
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
              reviewSectionsCompleted: { B1: true, createSummary: false },
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

      fixture = TestBed.createComponent(SendReportComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all html elements', () => {
      expect(page.heading1.textContent.trim()).toEqual('Submit responses');
      expect(page.paragraph.textContent.trim()).toEqual('You need to complete all tasks before submitting the report.');
    });
  });
});
