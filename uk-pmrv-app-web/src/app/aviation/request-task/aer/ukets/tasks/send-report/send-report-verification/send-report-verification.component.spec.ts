import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AccountVerificationBodyService, TasksService } from 'pmrv-api';

import { mockState } from '../testing/mock-state';
import { SendReportVerificationComponent } from './send-report-verification.component';

describe('SendReportVerificationComponent', () => {
  let component: SendReportVerificationComponent;
  let fixture: ComponentFixture<SendReportVerificationComponent>;
  let page: Page;
  let store: RequestTaskStore;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;
  let router: Router;
  const activatedRouteStub = new ActivatedRouteStub();

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendReportVerificationComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
    get paragraphsContent() {
      return this.queryAll('p').map((p) => p.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccount: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    store.setState(mockState);
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 210, name: 'Verifier' }));
    fixture = TestBed.createComponent(SendReportVerificationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);

    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual('Send report for verification');
    expect(page.paragraphsContent[0]).toEqual('Verifier');
  });

  it('should submit', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_AER_UKETS_REQUEST_VERIFICATION',
      requestTaskId: mockState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'AVIATION_AER_UKETS_REQUEST_VERIFICATION_PAYLOAD',
        verificationSectionsCompleted: {},
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../confirmation-verifier'], { relativeTo: activatedRouteStub });
  });

  it('should submit amends', () => {
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          type: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT',
          payload: {
            ...mockState.requestTaskItem.requestTask.payload,
            payloadType: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
            verificationSectionsCompleted: {},
          },
        },
      },
    } as any);

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_AER_UKETS_REQUEST_AMENDS_VERIFICATION',
      requestTaskId: mockState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'AVIATION_AER_UKETS_REQUEST_AMENDS_VERIFICATION_PAYLOAD',
        verificationSectionsCompleted: {},
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../confirmation-verifier'], { relativeTo: activatedRouteStub });
  });
});
