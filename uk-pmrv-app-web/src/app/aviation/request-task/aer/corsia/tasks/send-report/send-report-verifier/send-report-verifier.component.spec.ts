import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage, mockClass, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import {
  AccountVerificationBodyService,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  TasksService,
} from 'pmrv-api';

import { SendReportVerifierComponent } from './send-report-verifier.component';

describe('SendReportVerificationComponent', () => {
  let component: SendReportVerifierComponent;
  let fixture: ComponentFixture<SendReportVerifierComponent>;
  let page: Page;
  let store: RequestTaskStore;

  const accountVerificationBodyService: MockType<AccountVerificationBodyService> = {
    getVerificationBodyOfAccount: jest.fn().mockReturnValue(of({ id: 210, name: 'Verifier' })),
  };
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendReportVerifierComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get heading3(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h3');
    }

    get paragraphsContent(): string[] {
      return this.queryAll<HTMLParagraphElement>('p.govuk-body').map((item) => item.textContent.trim());
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
      imports: [RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    const state = store.getState();
    store.setState({
      ...state,
      isEditable: true,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestInfo: { id: 'AEM00055-2022', type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          id: 1,
          type: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD',
            aer: {},
            aerSectionsCompleted: {},
          } as AviationAerCorsiaApplicationSubmitRequestTaskPayload,
        },
      },
    });
    fixture = TestBed.createComponent(SendReportVerifierComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.heading1.textContent.trim()).toEqual('Send report for verification');
    expect(page.paragraphsContent).toEqual([
      'Verifier',
      'By selecting ‘Confirm and send’ you confirm that the information in your report is correct to the best of your knowledge.',
    ]);
    expect(page.submitButton).toBeTruthy();
  });

  it('should submit request and set accordingly properties', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_AER_CORSIA_REQUEST_VERIFICATION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'AVIATION_AER_CORSIA_REQUEST_VERIFICATION_PAYLOAD',
      },
    });
  });

  it('should display all html elements when it has been submitted', () => {
    component.isSubmitted$.next(true);
    fixture.detectChanges();
    expect(page.heading1.textContent.trim()).toEqual('Report sent for verification');
    expect(page.panelBody.textContent.trim()).toEqual('Your reference code is: AEM00055-2022');
    expect(page.heading3.textContent.trim()).toEqual('What happens next');
    expect(page.paragraphsContent).toEqual([
      'Your report has been sent to Verifier. You can recall your report at any time before Verifier returns it to you. Verifier will return the report to you once they have added an opinion statement. You will then be able to submit your report to the .',
    ]);
    expect(page.submitButton).toBeFalsy();
  });
});
