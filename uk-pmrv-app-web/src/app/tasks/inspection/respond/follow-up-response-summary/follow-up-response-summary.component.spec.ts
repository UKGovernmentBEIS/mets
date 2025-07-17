import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import {
  inspectionForRespondMockPostBuild,
  inspectionRespondMockState,
  mockInspectionRespondRequestTaskPayload,
} from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { FollowUpResponseSummaryComponent } from './follow-up-response-summary.component';

describe('FollowUpResponseSummaryComponent', () => {
  let component: FollowUpResponseSummaryComponent;
  let fixture: ComponentFixture<FollowUpResponseSummaryComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  class Page extends BasePage<FollowUpResponseSummaryComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'onsite', actionId: '1' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FollowUpResponseSummaryComponent],
      providers: [
        TaskTypeToBreadcrumbPipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(inspectionRespondMockState);

    fixture = TestBed.createComponent(FollowUpResponseSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.summaryValues).toEqual([
      ['Action type', 'Non-conformity'],
      ['Explanation', 'Vitae facere est as'],
      ['Completed action?', 'Yes'],
      ['Progress update', 'Action response 2 explanation'],
      ['Date action was completed', '20 Mar 2026'],
    ]);
  });

  it('should submit', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.submitButton).toBeTruthy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      inspectionForRespondMockPostBuild(
        {
          followupActionsResponses: {
            ...mockInspectionRespondRequestTaskPayload.followupActionsResponses,
          },
        },
        { 1: true },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../'], { relativeTo: activatedRoute });
  });
});
