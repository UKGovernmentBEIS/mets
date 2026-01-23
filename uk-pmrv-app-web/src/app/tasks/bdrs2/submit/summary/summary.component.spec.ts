import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { of } from 'rxjs';

import { BDRS2BaselineSummaryTemplateComponent } from '@shared/components/bdrs2/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../testing/mock-state';
import { BDRS2SummaryComponent } from './summary.component';

describe('BDRS2SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: BDRS2SummaryComponent;
  let fixture: ComponentFixture<BDRS2SummaryComponent>;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<BDRS2SummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get summaryTemplate() {
      return this.query('app-bdrs2-baseline-summary-template');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        TaskSharedModule,
        BdrS2TaskSharedModule,
        RouterLink,
        BDRS2BaselineSummaryTemplateComponent,
      ],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockStateBuild({
        bdrs2: {
          bdrs2guardQuestions: {
            applicationWithdrawalReason: undefined,
            continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
            covidAdjustments: true,
            inEiteSector: true,
            requiresAdditionalSubInstallationSplitsForCbam: true,
          },
          bdrs2Files: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
          mmpFiles: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
        },
        bdrs2SectionsCompleted: { baseline: false },
        bdrs2Attachments: { 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5': 'test.txt' },
      }),
    );
    fixture = TestBed.createComponent(BDRS2SummaryComponent);
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
      mockPostBuild({
        bdrs2: {
          bdrs2guardQuestions: {
            applicationWithdrawalReason: undefined,
            continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
            covidAdjustments: true,
            inEiteSector: true,
            requiresAdditionalSubInstallationSplitsForCbam: true,
          },
          bdrs2Files: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
          mmpFiles: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
        },
        bdrs2SectionsCompleted: { baseline: true },
      }),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: activatedRoute });
  });
});
