import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { BaselineSummaryTemplateComponent } from '@shared/components/bdr/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { mockState } from '../../testing/mock-state';
import { OutcomeSummaryComponent } from './outcome-summary.component';

describe('OutcomeSummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: OutcomeSummaryComponent;
  let fixture: ComponentFixture<OutcomeSummaryComponent>;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<OutcomeSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, BaselineSummaryTemplateComponent],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
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
            regulatorReviewGroupDecisions: {},
            regulatorReviewSectionsCompleted: { outcome: false },
            regulatorReviewOutcome: {
              freeAllocationNotes: 'My notes',
              freeAllocationNotesOperator: null,
              bdrFile: '22222222-2222-4222-a222-222222222222',
              files: ['11111111-1111-4111-a111-111111111111'],
            },
            regulatorReviewAttachments: {
              '11111111-1111-4111-a111-111111111111': 'file1.txt',
              '22222222-2222-4222-a222-222222222222': 'file2.jpg',
            },
          } as BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
        },
      },
    });
    fixture = TestBed.createComponent(OutcomeSummaryComponent);
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
    expect(page.summaryListValues).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'BDR_REGULATOR_REVIEW_SAVE',
      requestTaskActionPayload: {
        payloadType: 'BDR_REGULATOR_REVIEW_SAVE_PAYLOAD',
        regulatorReviewOutcome: {
          freeAllocationNotes: 'My notes',
          freeAllocationNotesOperator: null,
          bdrFile: '22222222-2222-4222-a222-222222222222',
          files: ['11111111-1111-4111-a111-111111111111'],
        },
        regulatorReviewSectionsCompleted: {
          outcome: true,
        },
      },
      requestTaskId: 1,
    });

    expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
  });
});
