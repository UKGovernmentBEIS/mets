import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskDTO, RequestTaskPayload, TasksService } from 'pmrv-api';

import { WithholdingAllowancesModule } from '../../withholding-allowances.module';
import { mockState } from '../testing/mock-state';
import { WithdrawReasonComponent } from './withdraw-reason.component';

describe('WithdrawReasonComponent', () => {
  let component: WithdrawReasonComponent;
  let fixture: ComponentFixture<WithdrawReasonComponent>;

  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const expectedReason = 'Some reason';
  const expectedNextRoute = '../summary';

  class Page extends BasePage<WithdrawReasonComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    set reason(value: string) {
      this.setInputValue('#reason', value);
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(WithdrawReasonComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WithholdingAllowancesModule, RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new withdraw details', () => {
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
              withholdingWithdrawal: {},
              sectionsCompleted: {},
            } as RequestTaskPayload,
          } as RequestTaskDTO,
        },
      });
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Provide reason for withdrawal details');
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([
        'Explain why you are withdrawing the withholding of allowances notice',
      ]);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to `summary`', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(1);

      page.reason = expectedReason;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionPayload: {
          payloadType: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION_PAYLOAD',
          sectionsCompleted: {
            WITHDRAWAL_REASON_CHANGE: false,
          },
          withholdingWithdrawal: {
            reason: 'Some reason',
          },
        },
        requestTaskActionType: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION',
        requestTaskId: 698,
      });
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });

  describe('for existing withdraw details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Provide reason for withdrawal details');
      expect(page.submitButton).toBeTruthy();
      expect(page.errorSummary).toBeFalsy();
    });

    it('should edit, submit a valid form and navigate to `summary`', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const expectedReasonEdited = 'Some reason edited';

      expect(page.errorSummary).toBeFalsy();

      page.reason = expectedReasonEdited;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionPayload: {
          payloadType: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION_PAYLOAD',
          sectionsCompleted: {
            WITHDRAWAL_REASON_CHANGE: false,
          },
          withholdingWithdrawal: {
            reason: 'Some reason edited',
          },
        },
        requestTaskActionType: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION',
        requestTaskId: 698,
      });
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });
});
