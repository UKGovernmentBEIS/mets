import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass, RouterStubComponent } from '@testing';

import { AccountVerificationBodyService, TasksService } from 'pmrv-api';

import { mockBdrS2State } from '../../testing/mock-bdrs2-payload';
import { Bdrs2SendReportVerifierComponent } from './send-report-verifier.component';

describe('Bdrs2SendReportVerifierComponent', () => {
  let component: Bdrs2SendReportVerifierComponent;
  let fixture: ComponentFixture<Bdrs2SendReportVerifierComponent>;
  let page: Page;
  let store: CommonTasksStore;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<Bdrs2SendReportVerifierComponent> {
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
      imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule, Bdrs2SendReportVerifierComponent],
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
      ...mockBdrS2State,
      requestTaskItem: {
        ...mockBdrS2State.requestTaskItem,
        requestTask: {
          ...mockBdrS2State.requestTaskItem.requestTask,
          payload: {
            ...mockBdrS2State.requestTaskItem.requestTask.payload,
            verificationSectionsCompleted: {},
          } as any,
        },
      },
    });
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 210, name: 'Verifier' }));

    fixture = TestBed.createComponent(Bdrs2SendReportVerifierComponent);
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
      requestTaskActionType: 'BDRS2_SUBMIT_TO_VERIFIER',
      requestTaskId: mockBdrS2State.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'BDRS2_SUBMIT_TO_VERIFIER_PAYLOAD',
        verificationSectionsCompleted: {},
      },
    });
  });
});
