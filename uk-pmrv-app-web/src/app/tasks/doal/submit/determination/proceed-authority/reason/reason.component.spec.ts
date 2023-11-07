import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../../testing';
import { DestroySubject } from '../../../../../../core/services/destroy-subject.service';
import { SharedModule } from '../../../../../../shared/shared.module';
import { TaskSharedModule } from '../../../../../shared/task-shared-module';
import { initialState } from '../../../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../../../store/common-tasks.store';
import { DoalTaskComponent } from '../../../../shared/components/doal-task/doal-task.component';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../../../test/mock';
import { ReasonComponent } from './reason.component';

describe('ReasonComponent', () => {
  let component: ReasonComponent;
  let fixture: ComponentFixture<ReasonComponent>;

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

  class Page extends BasePage<ReasonComponent> {
    get articleReasonGroupTypeRadios(): HTMLInputElement[] {
      return this.queryAll<HTMLInputElement>('input[name$="articleReasonGroupType"]');
    }

    get article6aReasonsCheckboxes(): HTMLInputElement[] {
      return this.queryAll<HTMLInputElement>('input[name$="article6aReasons"]');
    }

    get article34HReasonItemsCheckboxes(): HTMLInputElement[] {
      return this.queryAll<HTMLInputElement>('input[name$="article34HReasonItems"]');
    }

    get reasonTextArea() {
      return this.query<HTMLInputElement>('textarea[name$="reason"]');
    }
    get reason(): string {
      return this.getInputValue('#reason');
    }
    set reason(value: string) {
      this.setInputValue('#reason', value);
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
    fixture = TestBed.createComponent(ReasonComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReasonComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        DestroySubject,
      ],
    }).compileComponents();
  });

  describe('for new reason', () => {
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
      expect(page.errorSummaryList).toEqual(['Select an option', 'Enter a comment']);

      page.articleReasonGroupTypeRadios[0].click();
      fixture.detectChanges();

      page.article6aReasonsCheckboxes[1].click();
      page.article6aReasonsCheckboxes[2].click();

      page.reason = 'A comment';

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
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            determination: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'withholding'], {
        relativeTo: activatedRoute,
      });
    });
  });

  describe('for editing reason', () => {
    beforeEach(() => {
      const route = new ActivatedRouteStub(
        {
          taskId: `${mockDoalApplicationSubmitRequestTaskItem.requestTask.id}`,
        },
        null,
        { sectionKey: 'determination' },
      );
      TestBed.overrideProvider(ActivatedRoute, { useValue: route });
    });

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
            determination: true,
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should submit updated data', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.articleReasonGroupTypeRadios[0].checked).toBeTruthy();
      expect(page.article6aReasonsCheckboxes[0].checked).toBeFalsy();
      expect(page.article6aReasonsCheckboxes[1].checked).toBeTruthy();
      expect(page.article6aReasonsCheckboxes[2].checked).toBeTruthy();
      expect(page.article6aReasonsCheckboxes[3].checked).toBeFalsy();
      expect(page.reason).toEqual('A comment');

      page.articleReasonGroupTypeRadios[1].click();
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select an option']);

      page.article34HReasonItemsCheckboxes[1].click();
      page.article34HReasonItemsCheckboxes[2].click();

      page.reason = 'new comment';
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
              articleReasonGroupType: 'ARTICLE_34H_REASONS',
              articleReasonItems: ['ERROR_IN_NEW_ENTRANT_DATA_REPORT', 'ERROR_IN_ACTIVITY_LEVEL_REPORT'],
              reason: 'new comment',
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            determination: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'withholding'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
