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
import { RecoveryDetailsComponent } from './recovery-details.component';

describe('RecoveryDetailsComponent', () => {
  let component: RecoveryDetailsComponent;
  let fixture: ComponentFixture<RecoveryDetailsComponent>;

  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  class Page extends BasePage<RecoveryDetailsComponent> {
    get year(): string {
      return this.getInputValue('#year');
    }
    set year(value: string) {
      this.setInputValue('#year', value);
    }

    get reasonType(): string {
      return this.getInputValue('#reasonType');
    }
    set reasonType(value: string) {
      this.setInputValue('#reasonType', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(RecoveryDetailsComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WithholdingAllowancesModule, RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new withholding of allowances', () => {
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
              withholdingOfAllowances: {},
              sectionsCompleted: {},
            } as RequestTaskPayload,
          } as RequestTaskDTO,
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit and redirect to summary', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select a year', 'Select a reason']);

      page.year = '2027';
      page.reasonType = 'INVESTIGATING_AN_ERROR_IN_AVIATION_ALLOCATION_TABLE';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionPayload: {
          payloadType: 'WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION_PAYLOAD',
          withholdingOfAllowances: {
            otherReason: null,
            reasonType: 'INVESTIGATING_AN_ERROR_IN_AVIATION_ALLOCATION_TABLE',
            regulatorComments: null,
            year: '2027',
          },
          sectionsCompleted: { DETAILS_CHANGE: false },
        },
        requestTaskActionType: 'WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION',
        requestTaskId: 698,
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
        relativeTo: TestBed.inject(ActivatedRoute),
        state: { notification: true },
      });
    });
  });

  describe('for exosting withholding of allowances', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements ', () => {
      expect(page.year).toEqual(2027);
      expect(page.reasonType).toEqual('DETERMINING_A_SURRENDER_APPLICATION');
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      component.form.markAsDirty();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
        relativeTo: TestBed.inject(ActivatedRoute),
        state: { notification: true },
      });
    });
  });
});
