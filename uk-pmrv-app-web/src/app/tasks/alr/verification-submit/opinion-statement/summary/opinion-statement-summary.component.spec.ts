import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { alrMockVerificationPostBuild, alrVerificationMockStateBuild } from '@tasks/alr/test/mock-verifier';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AlrOpinionStatementSummaryComponent } from './opinion-statement-summary.component';

describe('SummaryComponent', () => {
  let component: AlrOpinionStatementSummaryComponent;
  let fixture: ComponentFixture<AlrOpinionStatementSummaryComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrOpinionStatementSummaryComponent> {
    get summaryTemplate() {
      return this.query('app-opinion-statement-summary-template');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrOpinionStatementSummaryComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      alrVerificationMockStateBuild(
        {
          opinionStatement: {
            opinionStatementFiles: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
            supportingFiles: [],
            notes: null,
          },
        },
        {
          opinionStatement: [false],
        },
        { 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5': 'test.txt' },
      ),
    );

    fixture = TestBed.createComponent(AlrOpinionStatementSummaryComponent);
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
          opinionStatement: {
            opinionStatementFiles: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
            supportingFiles: [],
            notes: null,
          },
          verificationSectionsCompleted: { opinionStatement: [true] },
        },
        {
          opinionStatement: [true],
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
