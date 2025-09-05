import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { hsetiMockReviewState } from '@tasks/hseti/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { DecisionSummaryComponent } from './decision-summary.component';

describe('DecisionSummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: DecisionSummaryComponent;
  let fixture: ComponentFixture<DecisionSummaryComponent>;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DecisionSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get summaryTemplate() {
      return this.query('app-overall-decision-summary-template');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionSummaryComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
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
            regulatorReviewSectionsCompleted: {
              OVERALL_DECISION: false,
              HSETI: true,
            },
          } as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
        } as any,
      },
    });
    fixture = TestBed.createComponent(DecisionSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Check your answers');
    expect(page.summaryTemplate).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        overallDecision: {
          reason: 'Approved for review',
          type: 'APPROVED',
        },
        payloadType: 'HSE_TI_REGULATOR_REVIEW_SAVE_PAYLOAD',
        regulatorReviewSectionsCompleted: {
          HSETI: true,
          OVERALL_DECISION: true,
        },
      },
      requestTaskActionType: 'HSE_TI_REGULATOR_REVIEW_SAVE',
      requestTaskId: 1,
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
