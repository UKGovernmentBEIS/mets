import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { SendReportComponent } from '@aviation/request-task/vir/respond/tasks/respond-item/send-report/send-report.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService, UncorrectedItem } from 'pmrv-api';

describe('SendReportComponent', () => {
  let page: Page;
  let component: SendReportComponent;
  let store: RequestTaskStore;
  let fixture: ComponentFixture<SendReportComponent>;

  const currentItem = {
    reference: 'B1',
    explanation: 'Test uncorrectedNonConformity',
    materialEffect: true,
  } as UncorrectedItem;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 19, id: currentItem.reference }, null, {
    verificationDataItem: currentItem,
  });
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SendReportComponent, RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    store.setState({
      requestTaskItem: {
        requestInfo: {
          id: 'VIR00001-2022',
        },
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
            virRespondToRegulatorCommentsSectionsCompleted: { B1: true },
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

    fixture = TestBed.createComponent(SendReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.isSubmitted$ = new BehaviorSubject<boolean>(false);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.heading1.textContent.trim()).toEqual('Submit response to B1');
    expect(page.paragraph.textContent.trim()).toEqual(
      'Are you sure you want to submit your response for this item to your regulator for assessment?',
    );
    expect(page.submitButton.textContent.trim()).toEqual('Confirm and complete');
  });

  it('should submit request and show notification banner', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS',
      requestTaskId: 19,
      requestTaskActionPayload: {
        payloadType: 'AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
        reference: 'B1',
        virRespondToRegulatorCommentsSectionsCompleted: { B1: true },
      },
    });

    expect(page.heading1.textContent.trim()).toEqual('Response submitted');
    expect(page.panelBody.textContent.trim()).toEqual(
      'Your responses have been successfully submitted to your regulator for assessmentYour reference code is: VIR00001-2022',
    );
    expect(page.heading3.textContent.trim()).toEqual('What happens next');
    expect(page.paragraph.textContent.trim()).toEqual(
      'Your regulator will review your responses and confirm any improvement requirement, where applicable.',
    );
    expect(page.submitButton).toBeFalsy();
  });
});
