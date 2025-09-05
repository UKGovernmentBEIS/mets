import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { hsetiMockReviewState } from '@tasks/hseti/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { HSETIOverallDecisionReviewReasonComponent } from './decision-reason.component';

describe('HSETIOverallDecisionReviewReasonComponent', () => {
  let component: HSETIOverallDecisionReviewReasonComponent;
  let fixture: ComponentFixture<HSETIOverallDecisionReviewReasonComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<HSETIOverallDecisionReviewReasonComponent> {
    get title() {
      return this.query<HTMLElement>('app-page-heading');
    }
    get reason() {
      return this.query<HTMLTextAreaElement>('textarea');
    }
  }

  describe('with approve overall decision', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [HseTiTaskSharedModule, SharedModule],
        providers: [{ provide: TasksService, useValue: tasksService }, provideRouter([])],
      }).compileComponents();

      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...hsetiMockReviewState,
        requestTaskItem: {
          ...hsetiMockReviewState.requestTaskItem,
          requestTask: {
            ...hsetiMockReviewState.requestTaskItem.requestTask,
            payload: {
              ...hsetiMockReviewState.requestTaskItem.requestTask.payload,
              regulatorReviewGroupDecisions: {
                HSETI: {
                  type: 'ACCEPTED',
                  details: {
                    notes: 'srfs',
                  } as any,
                },
              },
              overallDecision: {
                type: 'APPROVED',
                reason: 'Approved for review',
              },
              regulatorReviewSectionsCompleted: {},
            } as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
          } as any,
        },
      });

      fixture = TestBed.createComponent(HSETIOverallDecisionReviewReasonComponent);
      component = fixture.componentInstance;

      page = new Page(fixture);
      fixture.detectChanges();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the correct title and reason', () => {
      expect(page.title.textContent.trim()).toEqual('Overall decisionApprove');
      expect(page.reason.value).toEqual('Approved for review');
    });
  });

  describe('with deemed withdrawn overall decision', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [HseTiTaskSharedModule, SharedModule],
        providers: [{ provide: TasksService, useValue: tasksService }, provideRouter([])],
      }).compileComponents();

      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...hsetiMockReviewState,
        requestTaskItem: {
          ...hsetiMockReviewState.requestTaskItem,
          requestTask: {
            ...hsetiMockReviewState.requestTaskItem.requestTask,
            payload: {
              ...hsetiMockReviewState.requestTaskItem.requestTask.payload,
              regulatorReviewGroupDecisions: {
                HSETI: {
                  type: 'ACCEPTED',
                  details: {
                    notes: 'srfs',
                  } as any,
                },
              },
              overallDecision: {
                type: 'DEEMED_WITHDRAWN',
                reason: 'reason for review',
              },
              regulatorReviewSectionsCompleted: {},
            } as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
          } as any,
        },
      });

      fixture = TestBed.createComponent(HSETIOverallDecisionReviewReasonComponent);
      component = fixture.componentInstance;

      page = new Page(fixture);
      fixture.detectChanges();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the correct title and reason', () => {
      expect(page.title.textContent.trim()).toEqual('Overall decisionDeemed withdrawn');
      expect(page.reason.value).toEqual('reason for review');
    });
  });
  describe('with withdrawn overall decision', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [HseTiTaskSharedModule, SharedModule],
        providers: [{ provide: TasksService, useValue: tasksService }, provideRouter([])],
      }).compileComponents();

      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...hsetiMockReviewState,
        requestTaskItem: {
          ...hsetiMockReviewState.requestTaskItem,
          requestTask: {
            ...hsetiMockReviewState.requestTaskItem.requestTask,
            payload: {
              ...hsetiMockReviewState.requestTaskItem.requestTask.payload,
              regulatorReviewGroupDecisions: {
                HSETI: {
                  type: 'ACCEPTED',
                  details: {
                    notes: 'srfs',
                  } as any,
                },
              },
              overallDecision: {
                type: 'WITHDRAWN',
                reason: 'reason for review',
              },
              regulatorReviewSectionsCompleted: {},
            } as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
          } as any,
        },
      });

      fixture = TestBed.createComponent(HSETIOverallDecisionReviewReasonComponent);
      component = fixture.componentInstance;

      page = new Page(fixture);
      fixture.detectChanges();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the correct title and reason', () => {
      expect(page.title.textContent.trim()).toEqual('Overall decisionWithdraw');
      expect(page.reason.value).toEqual('reason for review');
    });
  });
  describe('with rejected overall decision', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [HseTiTaskSharedModule, SharedModule],
        providers: [{ provide: TasksService, useValue: tasksService }, provideRouter([])],
      }).compileComponents();

      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...hsetiMockReviewState,
        requestTaskItem: {
          ...hsetiMockReviewState.requestTaskItem,
          requestTask: {
            ...hsetiMockReviewState.requestTaskItem.requestTask,
            payload: {
              ...hsetiMockReviewState.requestTaskItem.requestTask.payload,
              regulatorReviewGroupDecisions: {
                HSETI: {
                  type: 'ACCEPTED',
                  details: {
                    notes: 'srfs',
                  } as any,
                },
              },
              overallDecision: {
                type: 'REJECTED',
                reason: 'reason for review',
              },
              regulatorReviewSectionsCompleted: {},
            } as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
          } as any,
        },
      });

      fixture = TestBed.createComponent(HSETIOverallDecisionReviewReasonComponent);
      component = fixture.componentInstance;

      page = new Page(fixture);
      fixture.detectChanges();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the correct title and reason', () => {
      expect(page.title.textContent.trim()).toEqual('Overall decisionReject');
      expect(page.reason.value).toEqual('reason for review');
    });
  });
});
