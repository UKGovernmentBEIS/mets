import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AccountVerificationBodyService, TasksService } from 'pmrv-api';

import { mockState } from '../testing/mock-state';
import { BdrCompleteReviewComponent } from './bdr-complete-review.component';
import { BdrCompleteConfirmationComponent } from './confirmation/bdr-complete-confirmation.component';

describe('BdrCompleteReviewComponent', () => {
  let component: BdrCompleteReviewComponent;
  let fixture: ComponentFixture<BdrCompleteReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<BdrCompleteReviewComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-xl');
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
      imports: [BdrTaskSharedModule],
      providers: [
        KeycloakService,
        provideRouter([{ path: 'confirmation', component: BdrCompleteConfirmationComponent }]),
        { provide: TasksService, useValue: tasksService },
        DestroySubject,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockState.requestTaskItem.requestTask.payload,
          } as any,
        },
      },
    });
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 210, name: 'Verifier' }));

    fixture = TestBed.createComponent(BdrCompleteReviewComponent);
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
    expect(page.heading.textContent.trim()).toEqual('Are you sure you want to complete the baseline data report task?');
  });

  it('should submit', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'BDR_REGULATOR_REVIEW_SUBMIT',
      requestTaskId: mockState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
