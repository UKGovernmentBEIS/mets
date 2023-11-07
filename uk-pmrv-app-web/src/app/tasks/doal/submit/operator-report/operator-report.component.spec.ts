import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { DoalTaskSectionKey } from '@tasks/doal/core/doal-task.type';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { OperatorReportComponent } from './operator-report.component';

describe('OperatorReportComponent', () => {
  let component: OperatorReportComponent;
  let fixture: ComponentFixture<OperatorReportComponent>;

  let page: Page;

  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const requestTaskAttachmentsHandlingService = mockClass(RequestTaskAttachmentsHandlingService);

  const route = new ActivatedRouteStub({ taskId: `${mockDoalApplicationSubmitRequestTaskItem.requestTask.id}` });

  class Page extends BasePage<OperatorReportComponent> {
    set documentFile(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }

    get documentFileText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }

    get areActivityLevelsEstimatedButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="areActivityLevelsEstimated"]');
    }

    get commentValue() {
      return this.getInputValue('#comment');
    }

    set commentValue(value: string) {
      this.setInputValue('#comment', value);
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
    fixture = TestBed.createComponent(OperatorReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OperatorReportComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: requestTaskAttachmentsHandlingService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new doal operator report', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockDoalApplicationSubmitRequestTaskItem,
          requestTask: {
            ...mockDoalApplicationSubmitRequestTaskItem.requestTask,
            payload: {
              ...mockDoalApplicationSubmitRequestTaskItem.requestTask.payload,
              doal: undefined,
              doalSectionsCompleted: {},
              doalAttachments: {},
            } as any,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      requestTaskAttachmentsHandlingService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: '2b587c89-1973-42ba-9682-b3ea5453b9dd' } })),
      );

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select a file', 'Select yes or no', 'Enter a comment']);

      page.areActivityLevelsEstimatedButtons[0].click();
      page.commentValue = 'A comment';
      page.documentFile = [new File(['fileBytes'], 'file1.txt')];
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();

      expect(requestTaskAttachmentsHandlingService.uploadRequestTaskAttachment).toHaveBeenCalledTimes(1);

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DOAL_SAVE_APPLICATION',
        requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
          doal: {
            operatorActivityLevelReport: {
              document: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
              areActivityLevelsEstimated: true,
              comment: 'A comment',
            },
          },
          doalSectionsCompleted: {
            operatorActivityLevelReport: false,
          } as { [key in DoalTaskSectionKey]: boolean },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing doal operator report', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {},
          {
            operatorActivityLevelReport: false,
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should display edit and submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();
      expect(page.areActivityLevelsEstimatedButtons[1].checked).toBeTruthy();
      expect(page.commentValue).toEqual('operatorActivityLevelReport');
      expect(page.documentFileText.map((row) => row.textContent.trim())).toEqual(['1.png']);

      page.areActivityLevelsEstimatedButtons[0].click();
      page.commentValue = 'new operatorActivityLevelReport';

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
            operatorActivityLevelReport: {
              document: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
              areActivityLevelsEstimated: true,
              comment: 'new operatorActivityLevelReport',
            },
          },
          doalSectionsCompleted: {
            operatorActivityLevelReport: false,
          } as { [key in DoalTaskSectionKey]: boolean },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
