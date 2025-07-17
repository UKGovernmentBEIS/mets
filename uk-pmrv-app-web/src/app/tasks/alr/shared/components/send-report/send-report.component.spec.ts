import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { AlrService } from '@tasks/alr/core';
import { alrSubmitMockState, mockAlrStateBuild } from '@tasks/alr/test/mock';
import { alrMockVerificationState, alrVerificationMockStateBuild } from '@tasks/alr/test/mock-verifier';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { AccountVerificationBodyService, TasksService } from 'pmrv-api';

import { AlrSendReportComponent } from './send-report.component';

describe('SendReportComponent', () => {
  let component: AlrSendReportComponent;
  let fixture: ComponentFixture<AlrSendReportComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrSendReportComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l').textContent.trim();
    }

    get paragraphsContent() {
      return this.queryAll('p[class="govuk-body"]').map((p) => p.textContent.trim());
    }

    get confirmationComponent() {
      return this.query<HTMLElement>('app-confirmation-shared');
    }

    get confirmationPanelHeading() {
      return this.confirmationComponent.querySelector<HTMLDivElement>('govuk-panel h1').textContent.trim();
    }

    get confirmationWhatHappensNextTemplate() {
      return this.confirmationComponent.querySelector<HTMLDivElement>('#whatHappensNextTemplate');
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
      imports: [AlrSendReportComponent],
      providers: [
        provideRouter([]),
        AlrService,
        { provide: TasksService, useValue: tasksService },
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
        CapitalizeFirstPipe,
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrStateBuild({
        alrSectionsCompleted: { activity: false },
      }),
    );

    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 210, name: 'Verifier' }));

    fixture = TestBed.createComponent(AlrSendReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display right content for operator side', () => {
    expect(page.confirmationComponent).toBeFalsy();
    expect(page.heading).toEqual('Send report for verification');
    expect(page.paragraphsContent).toEqual([
      'Verifier',
      'By selecting ‘Confirm and send’ you confirm that the information in your report is correct to the best of your knowledge.',
    ]);
  });

  it('should submit for operator side', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.confirmationComponent).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'ALR_SUBMIT_TO_VERIFIER',
      requestTaskId: alrSubmitMockState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'ALR_SUBMIT_TO_VERIFIER_PAYLOAD',
      },
    });
    expect(page.confirmationComponent).toBeTruthy();
    expect(page.confirmationPanelHeading).toEqual('Sent to verifier for review');
    expect(page.confirmationWhatHappensNextTemplate).toBeFalsy();
  });

  it('should display right content for verifier side', () => {
    store.setState(
      alrVerificationMockStateBuild({
        verificationSectionsCompleted: { opinionStatement: true, overallDecision: true },
      }),
    );

    fixture.detectChanges();

    expect(page.confirmationComponent).toBeFalsy();
    expect(page.heading).toEqual('Send verification report to the operator');
    expect(page.paragraphsContent).toEqual([
      'By selecting ‘Confirm and send’ you confirm that the information you have provided in this verification report is correct to the best of your knowledge.',
    ]);
  });

  it('should submit for verifier side', () => {
    store.setState(
      alrVerificationMockStateBuild({
        verificationSectionsCompleted: { opinionStatement: true, overallDecision: true },
      }),
    );

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.confirmationComponent).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'ALR_SUBMIT_VERIFICATION',
      requestTaskId: alrMockVerificationState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
    expect(page.confirmationComponent).toBeTruthy();
    expect(page.confirmationPanelHeading).toEqual('Verification report sent to operator');
    expect(page.confirmationWhatHappensNextTemplate).toBeTruthy();
  });
});
