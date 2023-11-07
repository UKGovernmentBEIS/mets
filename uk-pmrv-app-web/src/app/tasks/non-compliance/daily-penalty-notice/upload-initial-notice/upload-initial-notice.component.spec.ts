import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { NonComplianceTaskComponent } from '../../shared/components/non-compliance-task/non-compliance-task.component';
import { mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem } from '../../test/mock';
import { UploadInitialNoticeComponent } from './upload-initial-notice.component';

describe('UploadInitialNoticeComponent', () => {
  let component: UploadInitialNoticeComponent;
  let fixture: ComponentFixture<UploadInitialNoticeComponent>;
  let store: CommonTasksStore;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let page: Page;

  const expectedNextRoute = '../summary';
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1', index: '0' });
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const uuid1 = '11111111-1111-4111-a111-111111111111';

  class Page extends BasePage<UploadInitialNoticeComponent> {
    get singleFileInput(): HTMLElement {
      return this.query('app-file-input');
    }

    set filesValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }

    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }

    get fileDeleteButtons() {
      return this.queryAll<HTMLButtonElement>('.moj-multi-file-upload__delete');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(UploadInitialNoticeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UploadInitialNoticeComponent, NonComplianceTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
        DestroySubject,
      ],
    }).compileComponents();
  });

  describe('for new daily penalty notice', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem,
          requestTask: {
            ...mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem.requestTask,
            payload: undefined,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });
    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.singleFileInput).toBeTruthy();
      expect(page.fileDeleteButtons).toEqual([]);
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual(['Select a file']);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to `summary` page', async () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: uuid1 } })),
      );

      page.filesValue = [new File(['test content 1'], 'testfile1.jpg')];
      fixture.detectChanges();

      expect(page.fileDeleteButtons).toHaveLength(1);
      expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['testfile1.jpg']);

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
