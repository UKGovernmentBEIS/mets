import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { alrMockReviewState, mockAlrReviewPostBuild, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { ALCInformationSummaryComponent } from './alc-information-summary.component';

describe('ALCInformationSummaryComponent', () => {
  let component: ALCInformationSummaryComponent;
  let fixture: ComponentFixture<ALCInformationSummaryComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ALCInformationSummaryComponent> {
    get values() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }

    get historicalActivityLevelData() {
      return this.getActivityLevelTable(0);
    }

    get activityLevelData() {
      return this.getActivityLevelTable(1);
    }

    private getActivityLevelTable(idx: number) {
      return Array.from(this.queryAll<HTMLTableRowElement>('table')[idx].querySelectorAll('tr'))
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }

    get preliminaryAllocationsData() {
      return this.queryAll<HTMLTableRowElement>('app-alr-allocation-list-template table tr')
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ALCInformationSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AlrTaskSharedModule],
      providers: [provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrReviewStateBuild({
        regulatorReviewOutcome: {
          ...(
            alrMockReviewState.requestTaskItem.requestTask
              .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
          )?.regulatorReviewOutcome,
        },
      }),
    );
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary data', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual(['No', 'A comment']);
  });

  it('should display activity levels', () => {
    expect(page.activityLevelData).toEqual([
      ['2022', 'Dolime', 'Regulator rejects adjustment', '11.55', 'Comments 1'],
      ['2023', 'Facing bricks', 'Cessation', '43.33', 'Comments 2'],
    ]);
  });

  it('should display preliminary allocations', () => {
    expect(page.preliminaryAllocationsData).toEqual([['2025', 'Aluminium', '10']]);
  });

  it('should submit status section', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockAlrReviewPostBuild(
        {
          regulatorReviewOutcome: {
            ...(
              alrMockReviewState.requestTaskItem.requestTask
                .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
            )?.regulatorReviewOutcome,
          },
        },
        {
          ALC: true,
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
