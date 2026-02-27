import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';

import {
  BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
  RequestTaskAttachmentsHandlingService,
  TasksService,
} from 'pmrv-api';

import { mockState } from '../../testing/mock-state';
import { OutcomeFileUploadComponent } from './outcome-file-upload.component';

describe('OutcomeFileUploadComponent', () => {
  let page: Page;
  let router: Router;
  let control: FormControl;
  let bdrFileControl: FormControl;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: OutcomeFileUploadComponent;
  let fixture: ComponentFixture<OutcomeFileUploadComponent>;

  const expectedNextRoute = '../summary';
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const uuid1 = '11111111-1111-4111-a111-111111111111';
  const uuid2 = '22222222-2222-4222-a222-222222222222';
  const renamedFileName = 'BDRS2-00046-2026-v1-uploaded by -Unknown.jpg';

  class Page extends BasePage<OutcomeFileUploadComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
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
    fixture = TestBed.createComponent(OutcomeFileUploadComponent);
    component = fixture.componentInstance;
    control = component.form.get('supportingFiles') as FormControl;
    bdrFileControl = component.form.get('file') as FormControl;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  describe('for new upload documents files details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...mockState,
        requestTaskItem: {
          ...mockState.requestTaskItem,
          requestTask: {
            ...mockState.requestTaskItem.requestTask,
            payload: {
              ...mockState.requestTaskItem.requestTask.payload,
              regulatorReviewGroupDecisions: {},
              regulatorReviewSectionsCompleted: {},
            } as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
          },
        },
      });
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Upload the stage 2 baseline data report file (optional)');
      expect(page.multipleFileInput).toBeTruthy();
      expect(page.fileDeleteButtons).toEqual([]);
      expect(page.submitButton).toBeTruthy();
    });

    it('should not display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a valid form and navigate to next page', async () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: uuid2 } })),
      );

      control.setValue([{ file: new File(['test content 1'], 'testfile1.jpg'), uuid: uuid1 }]);
      bdrFileControl.setValue({ file: new File(['test bdr file content 1'], 'bdrfile-testfile1.jpg'), uuid: uuid2 });
      page.filesValue = [new File(['test content 2'], 'testfile2.jpg')];
      fixture.detectChanges();

      expect(page.fileDeleteButtons).toHaveLength(2);
      expect(page.filesText.map((row) => row.textContent.trim())).toEqual([renamedFileName, 'testfile1.jpg']);

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionPayload: {
          payloadType: 'BDRS2_REGULATOR_REVIEW_SAVE_PAYLOAD',
          regulatorReviewOutcome: {
            file: '22222222-2222-4222-a222-222222222222',
            supportingFiles: ['11111111-1111-4111-a111-111111111111'],
          },
          regulatorReviewSectionsCompleted: { outcome: false },
        },
        requestTaskActionType: 'BDRS2_REGULATOR_REVIEW_SAVE',
        requestTaskId: 1,
      });
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
