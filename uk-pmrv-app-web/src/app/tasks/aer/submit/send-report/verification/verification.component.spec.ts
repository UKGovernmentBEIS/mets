import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { VerificationComponent } from '@tasks/aer/submit/send-report/verification/verification.component';
import { mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AccountVerificationBodyService, TasksService } from 'pmrv-api';

describe('VerificationComponent', () => {
  let component: VerificationComponent;
  let fixture: ComponentFixture<VerificationComponent>;
  let page: Page;
  let store: CommonTasksStore;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<VerificationComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
    get paragraphsContent() {
      return this.queryAll('p[class="govuk-body"]').map((p) => p.textContent.trim());
    }
    get confirmationTitle() {
      return this.query<HTMLHeadingElement>('h1.govuk-panel__title');
    }
    get confirmationPanelContent() {
      return this.query<HTMLElement>('.govuk-panel__body');
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
      imports: [AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 210, name: 'Verifier' }));

    fixture = TestBed.createComponent(VerificationComponent);
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

    expect(page.confirmationTitle).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AER_REQUEST_VERIFICATION',
      requestTaskId: mockState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'AER_REQUEST_VERIFICATION_PAYLOAD',
        verificationSectionsCompleted: {},
      },
    });

    expect(page.confirmationTitle).toBeTruthy();
    expect(page.confirmationTitle.textContent.trim()).toEqual('Report sent for verification');
    expect(page.confirmationPanelContent.textContent.trim()).toEqual('Your reference is AEM210-2021');
    expect(page.paragraphsContent).toEqual([
      'Your report has been sent to Verifier.',
      'Verifier will return the report to you once they have added an opinion statement. You will then be able to submit your report to Natural Resources Wales.',
    ]);
  });
});
