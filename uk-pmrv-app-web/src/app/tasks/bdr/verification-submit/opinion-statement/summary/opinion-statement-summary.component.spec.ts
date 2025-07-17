import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { OpinionStatementSummaryComponent } from './opinion-statement-summary.component';

describe('OpinionStatementSummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: OpinionStatementSummaryComponent;
  let fixture: ComponentFixture<OpinionStatementSummaryComponent>;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<OpinionStatementSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get summaryTemplate() {
      return this.query('app-opinion-statement-summary-template');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementSummaryComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockStateBuild(
        {
          opinionStatement: {
            opinionStatementFiles: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
            notes: null,
          },
        },
        {
          opinionStatement: [false],
        },
        { 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5': 'test.txt' },
      ),
    );
    fixture = TestBed.createComponent(OpinionStatementSummaryComponent);
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
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          opinionStatement: {
            opinionStatementFiles: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
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
