import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { TaskSharedModule } from '../../../../../shared/task-shared-module';
import { initialState } from '../../../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../../../store/common-tasks.store';
import { DoalTaskComponent } from '../../../../shared/components/doal-task/doal-task.component';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../../../test/mock';
import { WithholdingComponent } from './withholding.component';

describe('WithholdingComponent', () => {
  let component: WithholdingComponent;
  let fixture: ComponentFixture<WithholdingComponent>;

  let page: Page;

  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {
      taskId: `${mockDoalApplicationSubmitRequestTaskItem.requestTask.id}`,
    },
    null,
    { sectionKey: 'determination' },
  );

  class Page extends BasePage<WithholdingComponent> {
    get hasWithholdingOfAllowancesRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="hasWithholdingOfAllowances"]');
    }

    get noticeIssuedDateDay() {
      return this.getInputValue('#withholdingAllowancesNotice.noticeIssuedDate-day');
    }
    set noticeIssuedDateDay(value: string) {
      this.setInputValue('#withholdingAllowancesNotice.noticeIssuedDate-day', value);
    }
    get noticeIssuedDateMonth() {
      return this.getInputValue('#withholdingAllowancesNotice.noticeIssuedDate-month');
    }
    set noticeIssuedDateMonth(value: string) {
      this.setInputValue('#withholdingAllowancesNotice.noticeIssuedDate-month', value);
    }
    get noticeIssuedDateYear() {
      return this.getInputValue('#withholdingAllowancesNotice.noticeIssuedDate-year');
    }
    set noticeIssuedDateYear(value: string) {
      this.setInputValue('#withholdingAllowancesNotice.noticeIssuedDate-year', value);
    }

    get withholdingOfAllowancesCommentTextArea() {
      return this.query<HTMLInputElement>(
        'textarea[name$="withholdingAllowancesNotice.withholdingOfAllowancesComment"]',
      );
    }
    get withholdingOfAllowancesComment(): string {
      return this.getInputValue('#withholdingAllowancesNotice.withholdingOfAllowancesComment');
    }
    set withholdingOfAllowancesComment(value: string) {
      this.setInputValue('#withholdingAllowancesNotice.withholdingOfAllowancesComment', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(WithholdingComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WithholdingComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new withholding', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            determination: {
              type: 'PROCEED_TO_AUTHORITY',
              reason: 'A comment',
              articleReasonGroupType: 'ARTICLE_6A_REASONS',
              articleReasonItems: [
                'SETTING_ALLOCATION_UNDER_ARTICLE_3A',
                'SETTING_HAL_AND_ALLOCATION_UNDER_ARTICLE_3A',
              ],
            } as any,
          },
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            determination: false,
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select yes or no']);

      page.hasWithholdingOfAllowancesRadios[0].click();
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummaryList).toEqual(['Enter a comment']);

      page.withholdingOfAllowancesComment = 'A comment';
      page.noticeIssuedDateDay = '10';
      page.noticeIssuedDateMonth = '10';
      page.noticeIssuedDateYear = '2026';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummaryList).toEqual(['The date must be today or in the past']);

      page.noticeIssuedDateYear = '2021';

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DOAL_SAVE_APPLICATION',
        requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
          doal: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
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
                noticeIssuedDate: new Date('2021-10-10'),
                withholdingOfAllowancesComment: 'A comment',
              },
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            determination: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'official-notice'], {
        relativeTo: activatedRoute,
      });
    });
  });

  describe('for existing withholding', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
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
            } as any,
          },
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            determination: false,
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should update', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.hasWithholdingOfAllowancesRadios[0].checked).toBeTruthy();
      expect(page.withholdingOfAllowancesComment).toEqual('withholdingOfAllowancesComment');
      expect(page.noticeIssuedDateDay).toEqual('10');
      expect(page.noticeIssuedDateMonth).toEqual('8');
      expect(page.noticeIssuedDateYear).toEqual('2022');

      page.hasWithholdingOfAllowancesRadios[1].click();
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DOAL_SAVE_APPLICATION',
        requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
          doal: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
            determination: {
              type: 'PROCEED_TO_AUTHORITY',
              reason: 'A comment',
              articleReasonGroupType: 'ARTICLE_6A_REASONS',
              articleReasonItems: [
                'SETTING_ALLOCATION_UNDER_ARTICLE_3A',
                'SETTING_HAL_AND_ALLOCATION_UNDER_ARTICLE_3A',
              ],
              hasWithholdingOfAllowances: false,
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            determination: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'official-notice'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
