import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { alrMockVerificationPostBuild, alrVerificationMockStateBuild } from '@tasks/alr/test/mock-verifier';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { OverallDecisionSummaryComponent } from './overall-decision-summary.component';

describe('OverallDecisionSummaryComponent', () => {
  let component: OverallDecisionSummaryComponent;
  let fixture: ComponentFixture<OverallDecisionSummaryComponent>;
  const activatedRoute = {
    params: of({}),
    queryParams: of({}),
    url: of([{ path: 'test-path' }]),
  };
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
      alrVerificationMockStateBuild({
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
      alrMockVerificationPostBuild(
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
