import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { mockNerPostBuild, nerSubmitMockState } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { NerDetailsUploadNerComponent } from './upload-ner.component';

describe('UploadNerComponent', () => {
  let component: NerDetailsUploadNerComponent;
  let fixture: ComponentFixture<NerDetailsUploadNerComponent>;
  let router: Router;
  let nerFileControl: FormControl;
  let supportingFilesControl: FormControl;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let page: Page;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const uuid1 = '11111111-1111-4111-a111-111111111111';
  const uuid2 = '22222222-2222-4222-a222-222222222222';

  class Page extends BasePage<NerDetailsUploadNerComponent> {
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerDetailsUploadNerComponent],
      providers: [
        CapitalizeFirstPipe,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(nerSubmitMockState);

    fixture = TestBed.createComponent(NerDetailsUploadNerComponent);
    component = fixture.componentInstance;
    supportingFilesControl = component.form.get('supportingFiles') as FormControl;
    nerFileControl = component.form.get('file') as FormControl;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements and form with 0 errors', () => {
    expect(page.errorSummary).toBeFalsy();
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

  it('should submit a valid form and navigate to next page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    attachmentService.uploadRequestTaskAttachment.mockReturnValue(
      asyncData<any>(new HttpResponse({ body: { uuid: uuid2 } })),
    );

    nerFileControl.setValue({
      file: new File(['test file content 1'], 'testfile1.jpg'),
      uuid: uuid2,
    });
    supportingFilesControl.setValue([{ file: new File(['test content 1'], 'testfile1.jpg'), uuid: uuid1 }]);
    page.filesValue = [new File(['test content 2'], 'testfile2.jpg')];
    fixture.detectChanges();

    expect(page.fileDeleteButtons).toHaveLength(2);
    expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
      'NER-00122-4-v1-vundefined-uploaded by -Unknown.jpg',
      'testfile1.jpg',
    ]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockNerPostBuild(
        {
          ner: {
            nerFiles: {
              file: uuid2,
              supportingFiles: ['11111111-1111-4111-a111-111111111111'],
            },
            mmpFiles: undefined,
            notes: null,
          },
        },
        {
          NER: false,
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['./', 'upload-mmp'], { relativeTo: activatedRoute });
  });
});
