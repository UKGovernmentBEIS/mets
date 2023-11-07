import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { asyncData, BasePage, mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { setStoreTask } from '../testing/set-store-task';
import { SiteDiagramComponent } from './site-diagram.component';

describe('SiteDiagramComponent', () => {
  let component: SiteDiagramComponent;
  let fixture: ComponentFixture<SiteDiagramComponent>;
  let tasksService: jest.Mocked<TasksService>;
  let attachmentService: jest.Mocked<RequestTaskAttachmentsHandlingService>;
  let store: PermitApplicationStore<PermitApplicationState>;

  let page: Page;

  class Page extends BasePage<SiteDiagramComponent> {
    get files() {
      return (
        this.query<HTMLSpanElement>('.moj-multi-file-upload__filename') ??
        this.query<HTMLSpanElement>('.moj-multi-file-upload__success')
      );
    }

    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }

    set fileValue(value: File) {
      this.setInputValue('input[type="file"]', value);
    }

    get fileDeleteButton() {
      return this.query<HTMLButtonElement>('.moj-multi-file-upload__delete');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLLIElement>('.govuk-error-summary__list > li');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SiteDiagramComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    tasksService = mockClass(TasksService);
    attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
    attachmentService.uploadRequestTaskAttachment.mockReturnValue(
      asyncData<any>(new HttpResponse({ body: { uuid: 'xzy' } })),
    );

    await TestBed.configureTestingModule({
      declarations: [SiteDiagramComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitIssuanceStore);
    store.setState({
      ...store.getState(),
      requestTaskId: 1,
      permitAttachments: { [mockPermitApplyPayload.permit.siteDiagrams[0]]: 'some-file.txt' },
      isEditable: true,
    });
    setStoreTask('siteDiagrams', mockPermitApplyPayload.permit.siteDiagrams, [true]);
    tasksService.processRequestTaskAction.mockReturnValue(asyncData({ data: { id: 'yui' } }));
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display an empty form if no task has no value', () => {
    setStoreTask('siteDiagrams', undefined, [true]);
    createComponent();

    expect(page.files).toBeFalsy();
  });

  it('should populate with the existing file', () => {
    expect(page.files.textContent).toEqual('some-file.txt');
  });

  it('should attach a file', async () => {
    const file = new File(['some content'], 'new-file.txt');
    page.fileValue = file;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
      'some-file.txt',
      'new-file.txt has been uploaded',
    ]);

    expect(attachmentService.uploadRequestTaskAttachment).toHaveBeenCalledWith(
      { requestTaskActionType: 'PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT', requestTaskId: 1 },
      file,
      'events',
      true,
    );

    page.submitButton.click();
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        permit: {
          ...store.permit,
          siteDiagrams: ['abc', 'xzy'],
        },
        permitSectionsCompleted: { siteDiagrams: [true] },
        payloadType: 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
      },
      requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_APPLICATION',
      requestTaskId: 1,
    });
    expect(TestBed.inject(PermitApplicationStore).getValue().permitAttachments).toMatchObject({ xzy: 'new-file.txt' });
  });

  it('should submit the form', () => {
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errors).toHaveLength(0);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        permit: {
          ...store.permit,
          siteDiagrams: mockPermitApplyPayload.permit.siteDiagrams,
        },
        permitSectionsCompleted: { siteDiagrams: [true] },
        payloadType: 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
      },
      requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_APPLICATION',
      requestTaskId: 1,
    });
    expect(TestBed.inject(PermitApplicationStore).getValue().permitAttachments).toEqual({
      [mockPermitApplyPayload.permit.siteDiagrams[0]]: 'some-file.txt',
    });
  });

  it('should submit a form without a file', () => {
    page.fileDeleteButton.click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errors).toHaveLength(0);
  });
});
