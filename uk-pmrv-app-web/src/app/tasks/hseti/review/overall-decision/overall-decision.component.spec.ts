import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { hsetiMockReviewState } from '@tasks/hseti/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { HSETIOverallDecisionReviewComponent } from './overall-decision.component';

describe('HSETIOverallDecisionReviewComponent', () => {
  let component: HSETIOverallDecisionReviewComponent;
  let fixture: ComponentFixture<HSETIOverallDecisionReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<HSETIOverallDecisionReviewComponent> {
    get buttons() {
      return this.queryAll<HTMLLIElement>('button');
    }

    get approveButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Approve')[0];
    }

    get deemWithdrawButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Deem withdrawn')[0];
    }

    get withdrawButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Withdrawn')[0];
    }

    get rejectButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Reject')[0];
    }

    get unavailableActionsHeader() {
      return this.query<HTMLHeadingElement>('h2.govuk-heading-m');
    }

    get unavailableActionsText() {
      return this.query<HTMLDivElement>('div.govuk-body');
    }
  }

  describe('with accepted details', () => {
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
              regulatorReviewSectionsCompleted: {},
            } as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
          } as any,
        },
      });

      fixture = TestBed.createComponent(HSETIOverallDecisionReviewComponent);
      component = fixture.componentInstance;

      page = new Page(fixture);
      fixture.detectChanges();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the buttons, approve, deem withrawn and withdrawn', () => {
      expect(page.buttons.length).toEqual(3);
      expect(page.buttons[0].innerHTML.trim()).toEqual('Approve');
      expect(page.buttons[1].innerHTML.trim()).toEqual('Deem withdrawn');
      expect(page.buttons[2].innerHTML.trim()).toEqual('Withdrawn');
    });

    it('should show the buttons, approve, deem withrawn and withdrawn', () => {
      expect(page.unavailableActionsHeader.innerHTML.trim()).toEqual('Unavailable actions');
      expect(page.unavailableActionsText.textContent.trim()).toEqual(
        'RejectAll sections must have a decision with one or more marked as rejected',
      );
    });
  });

  describe('with rejected details', () => {
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
                  type: 'REJECTED',
                  details: {
                    notes: 'srfs',
                  } as any,
                },
              },
              regulatorReviewSectionsCompleted: {},
            } as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
          } as any,
        },
      });

      fixture = TestBed.createComponent(HSETIOverallDecisionReviewComponent);
      component = fixture.componentInstance;

      page = new Page(fixture);
      fixture.detectChanges();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the buttons, approve, deem withrawn and withdrawn', () => {
      expect(page.buttons.length).toEqual(2);
      expect(page.buttons[0].innerHTML.trim()).toEqual('Reject');
      expect(page.buttons[1].innerHTML.trim()).toEqual('Withdrawn');
    });

    it('should show the buttons, approve, deem withrawn and withdrawn', () => {
      expect(page.unavailableActionsHeader.innerHTML.trim()).toEqual('Unavailable actions');
      expect(page.unavailableActionsText.textContent.trim()).toEqual('ApproveAll sections must be marked as accepted.');
    });
  });
});
