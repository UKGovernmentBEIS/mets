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

import { VerificationReportComponent } from './verification-report.component';

describe('VerificationReportComponent', () => {
  let component: VerificationReportComponent;
  let fixture: ComponentFixture<VerificationReportComponent>;

  let page: Page;

  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const requestTaskAttachmentsHandlingService = mockClass(RequestTaskAttachmentsHandlingService);

  const route = new ActivatedRouteStub({ taskId: `${mockDoalApplicationSubmitRequestTaskItem.requestTask.id}` });

  class Page extends BasePage<VerificationReportComponent> {
    set documentFile(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }

    get documentFileText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
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
    fixture = TestBed.createComponent(VerificationReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerificationReportComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: requestTaskAttachmentsHandlingService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new doal verification report', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
            verificationReportOfTheActivityLevelReport: undefined,
          },
          {},
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

      requestTaskAttachmentsHandlingService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: 'abf68262-f6d1-4137-b654-c3302079d023' } })),
      );

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select a file', 'Enter a comment']);

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
            ...(store.getState().requestTaskItem.requestTask.payload as any).doal,
            verificationReportOfTheActivityLevelReport: {
              document: 'abf68262-f6d1-4137-b654-c3302079d023',
              comment: 'A comment',
            },
          },
          doalSectionsCompleted: {
            ...(store.getState().requestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            verificationReportOfTheActivityLevelReport: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing doal verification report', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {},
          {
            verificationReportOfTheActivityLevelReport: false,
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should display edit and submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();
      expect(page.commentValue).toEqual('verificationReportOfTheActivityLevelReportComment');
      expect(page.documentFileText.map((row) => row.textContent.trim())).toEqual(['2.png']);

      page.commentValue = 'new verification comment';

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
            verificationReportOfTheActivityLevelReport: {
              document: 'abf68262-f6d1-4137-b654-c3302079d023',
              comment: 'new verification comment',
            },
          },
          doalSectionsCompleted: {
            verificationReportOfTheActivityLevelReport: false,
          } as { [key in DoalTaskSectionKey]: boolean },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
