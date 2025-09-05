import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { alrMockReviewState, mockAlrReviewPostBuild, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { AlrDeterminationSummaryComponent } from './summary.component';

describe('AlrDeterminationSummaryComponent', () => {
  let component: AlrDeterminationSummaryComponent;
  let fixture: ComponentFixture<AlrDeterminationSummaryComponent>;

  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrDeterminationSummaryComponent> {
    get values() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrDeterminationSummaryComponent);
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

  describe('Close type', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockAlrReviewStateBuild({
          regulatorReviewOutcome: {
            ...(
              alrMockReviewState.requestTaskItem.requestTask
                .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
            )?.regulatorReviewOutcome,
            determination: {
              type: 'CLOSED_ALR',
              reason: 'A comment',
              alrFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
              files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
            },
          },
          regulatorReviewAttachments: { 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5': 'testFile.txt' },
          regulatorReviewSectionsCompleted: { ALC: true, DETERMINATION: false },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display data', () => {
      expect(page.values.map((el) => el.textContent.trim())).toEqual([
        'Close task',
        'A comment',
        'testFile.txt',
        'testFile.txt',
      ]);
    });

    it('should submit status section true', () => {
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
              determination: {
                type: 'CLOSED_ALR',
                reason: 'A comment',
                alrFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
                files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
              },
            },
          },
          {
            ALC: true,
            DETERMINATION: true,
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: route });
    });
  });

  describe('Proceed authority type', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockAlrReviewStateBuild({
          regulatorReviewOutcome: {
            ...(
              alrMockReviewState.requestTaskItem.requestTask
                .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
            )?.regulatorReviewOutcome,
            determination: {
              type: 'PROCEED_TO_AUTHORITY',
              reason: 'A comment',
              articleReasonGroupType: 'ARTICLE_6A_REASONS',
              articleReasonItems: [
                'SETTING_ALLOCATION_UNDER_ARTICLE_3A',
                'SETTING_HAL_AND_ALLOCATION_UNDER_ARTICLE_3A',
              ],
              hasWithholdingOfAllowances: true,
              withholdingAllowancesNotice: {
                noticeIssuedDate: '2022-08-10',
                withholdingOfAllowancesComment: 'withholdingOfAllowancesComment',
              },
              needsOfficialNotice: true,
            },
          },
          regulatorReviewSectionsCompleted: { ALC: true, DETERMINATION: false },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display data', () => {
      expect(page.values.map((el) => el.textContent.trim())).toEqual([
        'Proceed to UK ETS authority',
        'Article 6a reasons  1.  Article 6a of the Activity Level Changes Regulation (setting allocation under Article 3a - for year in which start of normal operation occurred only of new sub-installation)  2.  Article 6a of the Activity Level Changes Regulation (setting HAL and allocation under Article 3a - after first full calendar year operation of new sub-installation)',
        'A comment',
        'Yes',
        '10 Aug 2022',
        'withholdingOfAllowancesComment',
        'Yes',
      ]);
    });

    it('should submit status section true', () => {
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
              determination: {
                type: 'PROCEED_TO_AUTHORITY',
                reason: 'A comment',
                articleReasonGroupType: 'ARTICLE_6A_REASONS',
                articleReasonItems: [
                  'SETTING_ALLOCATION_UNDER_ARTICLE_3A',
                  'SETTING_HAL_AND_ALLOCATION_UNDER_ARTICLE_3A',
                ],
                hasWithholdingOfAllowances: true,
                withholdingAllowancesNotice: {
                  noticeIssuedDate: '2022-08-10',
                  withholdingOfAllowancesComment: 'withholdingOfAllowancesComment',
                },
                needsOfficialNotice: true,
              },
            },
          },
          {
            ALC: true,
            DETERMINATION: true,
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: route });
    });
  });
});
