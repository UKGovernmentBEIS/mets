import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../../test/mock';
import { CommentsComponent } from './comments.component';

describe('CommentsComponent', () => {
  let component: CommentsComponent;
  let fixture: ComponentFixture<CommentsComponent>;

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
    { sectionKey: 'activityLevelChangeInformation' },
  );

  class Page extends BasePage<CommentsComponent> {
    get commentsForUkEtsAuthorityTextArea() {
      return this.query<HTMLInputElement>('textarea[name$="commentsForUkEtsAuthority"]');
    }

    get commentsForUkEtsAuthority(): string {
      return this.getInputValue('#commentsForUkEtsAuthority');
    }
    set commentsForUkEtsAuthority(value: string) {
      this.setInputValue('#commentsForUkEtsAuthority', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(CommentsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CommentsComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new comment', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            activityLevelChangeInformation: {
              ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal
                .activityLevelChangeInformation,
              commentsForUkEtsAuthority: null,
            },
          },
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            activityLevelChangeInformation: false,
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

      page.commentsForUkEtsAuthority = 'A comment';

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
            activityLevelChangeInformation: {
              ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal
                .activityLevelChangeInformation,
              commentsForUkEtsAuthority: 'A comment',
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            activityLevelChangeInformation: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'summary'], {
        relativeTo: activatedRoute,
        state: { enableViewSummary: true },
      });
    });
  });

  describe('for editing comments', () => {
    beforeEach(() => {
      const route = new ActivatedRouteStub(
        {
          taskId: `${mockDoalApplicationSubmitRequestTaskItem.requestTask.id}`,
        },
        null,
        { sectionKey: 'activityLevelChangeInformation' },
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
            activityLevelChangeInformation: {
              ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal
                .activityLevelChangeInformation,
              commentsForUkEtsAuthority: 'a comment',
            },
          },
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            activityLevelChangeInformation: false,
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should submit updated data', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.commentsForUkEtsAuthority = 'new comment';
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
            activityLevelChangeInformation: {
              ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal
                .activityLevelChangeInformation,
              commentsForUkEtsAuthority: 'new comment',
            },
          },
          doalSectionsCompleted: {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            activityLevelChangeInformation: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'summary'], {
        relativeTo: activatedRoute,
        state: { enableViewSummary: true },
      });
    });
  });
});
