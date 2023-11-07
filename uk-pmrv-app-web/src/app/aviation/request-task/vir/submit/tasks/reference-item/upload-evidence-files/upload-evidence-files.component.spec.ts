import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemFormProvider } from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item-form.provider';
import { UploadEvidenceFilesComponent } from '@aviation/request-task/vir/submit/tasks/reference-item/upload-evidence-files/upload-evidence-files.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';

import {
  OperatorImprovementResponse,
  RequestTaskAttachmentsHandlingService,
  TasksService,
  UncorrectedItem,
} from 'pmrv-api';

describe('UploadEvidenceFilesComponent', () => {
  let page: Page;
  let router: Router;
  let control: FormControl;
  let store: RequestTaskStore;
  let component: UploadEvidenceFilesComponent;
  let fixture: ComponentFixture<UploadEvidenceFilesComponent>;
  let formProvider: ReferenceItemFormProvider;

  const currentItem = {
    reference: 'B1',
    explanation: 'Test uncorrectedNonConformity',
    materialEffect: true,
  } as UncorrectedItem;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 19, id: currentItem.reference }, null, {
    verificationDataItem: currentItem,
  });
  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const uuid1 = '11111111-1111-4111-a111-111111111111';
  const uuid2 = '22222222-2222-4222-a222-222222222222';

  class Page extends BasePage<UploadEvidenceFilesComponent> {
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
    fixture = TestBed.createComponent(UploadEvidenceFilesComponent);
    component = fixture.componentInstance;
    control = component.form.get('files') as FormControl;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadEvidenceFilesComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ReferenceItemFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  describe('for new upload evidence files details', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<ReferenceItemFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestTask: {
            id: 19,
            type: 'AVIATION_VIR_APPLICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
              verificationData: {
                uncorrectedNonConformities: {
                  B1: currentItem,
                },
              },
              virSectionsCompleted: { B1: false },
              operatorImprovementResponses: {
                B1: {
                  isAddressed: false,
                  addressedDescription: 'Test description B1, when no',
                  uploadEvidence: true,
                  files: [],
                },
              },
            },
          },
        },
      } as any);
      formProvider.setFormValue(
        store.virDelegate.payload.operatorImprovementResponses['B1'] as OperatorImprovementResponse,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Upload evidence and documents: B1');
      expect(page.multipleFileInput).toBeTruthy();
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

    it('should submit a valid form and navigate to `upload-evidence-files` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: uuid2 } })),
      );

      control.setValue([{ file: new File(['test content 1'], 'testfile1.jpg'), uuid: uuid1 }]);
      page.filesValue = [new File(['test content 2'], 'testfile2.jpg')];
      fixture.detectChanges();

      expect(page.fileDeleteButtons).toHaveLength(2);
      expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
        'testfile1.jpg',
        'testfile2.jpg has been uploaded',
      ]);

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_VIR_SAVE_APPLICATION',
        requestTaskActionPayload: {
          operatorImprovementResponses: {
            B1: {
              isAddressed: false,
              addressedDescription: 'Test description B1, when no',
              addressedDate: null,
              uploadEvidence: true,
              files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
            },
          },
          virSectionsCompleted: { B1: false },
          payloadType: 'AVIATION_VIR_SAVE_APPLICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });
  });
});
