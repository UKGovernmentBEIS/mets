import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { OverallDecisionSummaryComponent } from '@tasks/bdr/verification-submit';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';

describe('OverallDecisionSummaryComponent', () => {
  let component: OverallDecisionSummaryComponent;
  let fixture: ComponentFixture<OverallDecisionSummaryComponent>;
  let activatedRoute: ActivatedRoute;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<OverallDecisionSummaryComponent> {
    get summaryTemplate() {
      return this.query('app-shared-overall-decision-summary-template');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverallDecisionSummaryComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockStateBuild({
        overallAssessment: { type: 'VERIFIED_WITH_COMMENTS', reasons: 'Comment' },
      }),
    );

    fixture = TestBed.createComponent(OverallDecisionSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.summaryTemplate).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          overallAssessment: { type: 'VERIFIED_WITH_COMMENTS', reasons: 'Comment' },
        },
        {
          overallDecision: [true],
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
