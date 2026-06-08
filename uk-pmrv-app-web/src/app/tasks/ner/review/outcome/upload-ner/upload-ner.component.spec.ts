import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { mockNerReviewPostBuild, nerReviewMockState } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { NerReviewUploadNerComponent } from './upload-ner.component';

describe('UploadNerReviewComponent', () => {
  let component: NerReviewUploadNerComponent;
  let fixture: ComponentFixture<NerReviewUploadNerComponent>;
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

  class Page extends BasePage<NerReviewUploadNerComponent> {
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

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerReviewUploadNerComponent],
      providers: [
        CapitalizeFirstPipe,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(nerReviewMockState);

    fixture = TestBed.createComponent(NerReviewUploadNerComponent);
    component = fixture.componentInstance;
    supportingFilesControl = component.form.get('supportingFiles') as FormControl;
    nerFileControl = component.form.get('nerFile') as FormControl;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements and form', () => {
    expect(page.multipleFileInput).toBeTruthy();
    expect(page.fileDeleteButtons).toEqual([]);
    expect(page.submitButton).toBeTruthy();
  });

  it('should submit a valid form and navigate to next page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    attachmentService.uploadRequestTaskAttachment.mockReturnValue(
      asyncData<any>(new HttpResponse({ body: { uuid: uuid2 } })),
    );

    nerFileControl.setValue({
      nerFile: new File(['test file content 1'], 'testfile1.jpg'),
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

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockNerReviewPostBuild(
        {
          regulatorReviewOutcome: {
            nerFile: uuid2,
            supportingFiles: ['11111111-1111-4111-a111-111111111111'],
          },
        },
        {
          OUTCOME: false,
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'summary'], { relativeTo: activatedRoute });
  });
});
