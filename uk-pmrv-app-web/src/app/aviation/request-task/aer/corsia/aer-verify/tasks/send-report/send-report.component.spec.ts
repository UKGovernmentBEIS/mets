import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { SendReportComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/send-report/send-report.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
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
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    store.setState({
      requestTaskItem: {
        requestInfo: {
          id: 'AER00001-2022',
        },
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            verificationReport: {},
            verificationSectionsCompleted: {},
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
    expect(page.heading1.textContent.trim()).toEqual('Send verification report to the operator');
    expect(page.paragraph.textContent.trim()).toEqual(
      'By selecting ‘confirm and send’ you are confirming that the information you have provided in this verification report is correct to the best of your knowledge.',
    );
    expect(page.submitButton.textContent.trim()).toEqual('Confirm and complete');
  });

  it('should submit request and show notification banner', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_AER_UKETS_SUBMIT_APPLICATION_VERIFICATION',
      requestTaskId: 19,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });

    expect(page.heading1.textContent.trim()).toEqual('Verification report sent to operator');
    expect(page.panelBody.textContent.trim()).toEqual('Your reference code is: AER00001-2022');
    expect(page.heading3.textContent.trim()).toEqual('What happens next');
    expect(page.submitButton).toBeFalsy();
  });
});
