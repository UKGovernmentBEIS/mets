import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { DoalTaskSectionKey } from '../../core/doal-task.type';
import { DoalTaskComponent } from '../../shared/components/doal-task/doal-task.component';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../test/mock';
import { AdditionalDocumentsComponent } from './additional-documents.component';

describe('AdditionalDocumentsComponent', () => {
  let component: AdditionalDocumentsComponent;
  let fixture: ComponentFixture<AdditionalDocumentsComponent>;

  let page: Page;

  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const requestTaskAttachmentsHandlingService = mockClass(RequestTaskAttachmentsHandlingService);

  const route = new ActivatedRouteStub({ taskId: `${mockDoalApplicationSubmitRequestTaskItem.requestTask.id}` });

  class Page extends BasePage<AdditionalDocumentsComponent> {
    get existButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get multipleFileInput(): HTMLElement {
      return this.query('app-multiple-file-input');
    }
    set filesValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }
    get filesText() {
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
    fixture = TestBed.createComponent(AdditionalDocumentsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdditionalDocumentsComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: requestTaskAttachmentsHandlingService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new additional documents', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
            additionalDocuments: undefined,
          },
          {},
        ),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit when no additional documents exist', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select yes or no']);

      page.existButtons[1].click();
      fixture.detectChanges();

      page.commentValue = 'A comment';

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DOAL_SAVE_APPLICATION',
        requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
          doal: {
            ...(store.getState().requestTaskItem.requestTask.payload as any).doal,
            additionalDocuments: {
              exist: false,
              comment: 'A comment',
            },
          },
          doalSectionsCompleted: {
            ...(store.getState().requestTaskItem.requestTask.payload as any).doalSectionsCompleted,
            additionalDocuments: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });

    it('should submit when additional documents exist', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      const uuid = '2b587c89-1973-42ba-9682-b3ea5453b9dd';

      requestTaskAttachmentsHandlingService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: uuid } })),
      );

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select yes or no']);

      page.existButtons[0].click();
      fixture.detectChanges();

      page.filesValue = [new File(['test content 1'], 'testfile1.jpg')];
      fixture.detectChanges();

      page.commentValue = 'A comment';

      page.submitButton.click();
      fixture.detectChanges();

      expect(requestTaskAttachmentsHandlingService.uploadRequestTaskAttachment).toHaveBeenCalledTimes(1);

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DOAL_SAVE_APPLICATION',
        requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
          doal: {
            ...(store.getState().requestTaskItem.requestTask.payload as any).doal,
            additionalDocuments: {
              exist: true,
              documents: [uuid],
              comment: 'A comment',
            },
          },
          doalSectionsCompleted: {
            additionalDocuments: false,
          } as { [key in DoalTaskSectionKey]: boolean },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing additional documents', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {},
          {
            additionalDocuments: false,
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should display edit and submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.commentValue).toEqual('additionalDocumentsComment');
      expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['3.png']);

      page.commentValue = 'new comment';
      fixture.detectChanges();

      page.existButtons[1].click();
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
            additionalDocuments: {
              exist: false,
              documents: undefined,
              comment: 'new comment',
            },
          },
          doalSectionsCompleted: {
            additionalDocuments: false,
          } as { [key in DoalTaskSectionKey]: boolean },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
