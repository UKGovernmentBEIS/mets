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
import { OfficialNoticeComponent } from './official-notice.component';

describe('OfficialNoticeComponent', () => {
  let component: OfficialNoticeComponent;
  let fixture: ComponentFixture<OfficialNoticeComponent>;

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

  class Page extends BasePage<OfficialNoticeComponent> {
    get needsOfficialNotice() {
      return this.queryAll<HTMLInputElement>('input[name$="needsOfficialNotice"]');
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
    fixture = TestBed.createComponent(OfficialNoticeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OfficialNoticeComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new official notice', () => {
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
              hasWithholdingOfAllowances: false,
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

      page.needsOfficialNotice[0].click();
      fixture.detectChanges();

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
              hasWithholdingOfAllowances: false,
              needsOfficialNotice: true,
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            determination: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'summary'], {
        relativeTo: activatedRoute,
      });
    });
  });

  describe('for existing official notice', () => {
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
              hasWithholdingOfAllowances: false,
              needsOfficialNotice: true,
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

      expect(page.needsOfficialNotice[0].checked).toBeTruthy();

      page.needsOfficialNotice[1].click();
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
              needsOfficialNotice: false,
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            determination: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'summary'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
