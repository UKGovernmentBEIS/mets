import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass, RouterStubComponent } from '@testing';

import { AccountVerificationBodyService, BDRApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { mockBdrState } from '../../testing/mock-bdr-payload';
import { SendReportVerifierComponent } from './send-report-verifier.component';

describe('SendReportVerifierComponent', () => {
  let component: SendReportVerifierComponent;
  let fixture: ComponentFixture<SendReportVerifierComponent>;
  let page: Page;
  let store: CommonTasksStore;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SendReportVerifierComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
    get paragraphsContent() {
      return this.queryAll('p[class="govuk-body"]').map((p) => p.textContent.trim());
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
      imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, SendReportVerifierComponent],
      providers: [
        provideRouter([{ path: 'confirmation', component: RouterStubComponent }]),
        { provide: TasksService, useValue: tasksService },
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...mockBdrState,
      requestTaskItem: {
        ...mockBdrState.requestTaskItem,
        requestTask: {
          ...mockBdrState.requestTaskItem.requestTask,
          payload: {
            ...mockBdrState.requestTaskItem.requestTask.payload,
            verificationSectionsCompleted: {},
          } as BDRApplicationSubmitRequestTaskPayload,
        },
      },
    });
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 210, name: 'Verifier' }));

    fixture = TestBed.createComponent(SendReportVerifierComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
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

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'BDR_SUBMIT_TO_VERIFIER',
      requestTaskId: mockBdrState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'BDR_SUBMIT_TO_VERIFIER_PAYLOAD',
        verificationSectionsCompleted: {},
      },
    });
  });
});
