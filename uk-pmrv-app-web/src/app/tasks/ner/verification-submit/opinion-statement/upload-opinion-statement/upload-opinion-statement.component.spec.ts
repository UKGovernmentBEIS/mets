import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { nerMockVerificationPostBuild, nerMockVerificationState } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { NerUploadOpinionStatementComponent } from './upload-opinion-statement.component';

describe('UploadOpinionStatementComponent', () => {
  let component: NerUploadOpinionStatementComponent;
  let fixture: ComponentFixture<NerUploadOpinionStatementComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const uuid1 = '22222222-2222-4222-a222-222222222222';

  class Page extends BasePage<NerUploadOpinionStatementComponent> {
    get multipleFileInputs(): HTMLElement[] {
      return this.queryAll('app-multiple-file-input');
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
      imports: [NerUploadOpinionStatementComponent],
      providers: [
        CapitalizeFirstPipe,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(nerMockVerificationState);

    fixture = TestBed.createComponent(NerUploadOpinionStatementComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements and form with 0 errors', () => {
    expect(page.errorSummary).toBeFalsy();
    expect(page.multipleFileInputs.length).toEqual(1);
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

  it('should submit a valid form and navigate to next page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    attachmentService.uploadRequestTaskAttachment.mockReturnValue(
      asyncData<any>(new HttpResponse({ body: { uuid: uuid1 } })),
    );

    page.filesValue = [new File(['test content 2'], 'testfile2.jpg')];
    fixture.detectChanges();

    expect(page.fileDeleteButtons).toHaveLength(1);
    expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['testfile2.jpg']);

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      nerMockVerificationPostBuild(
        {
          opinionStatement: {
            opinionStatementFile: uuid1,
            supportingFiles: [],
            notes: null,
          },
        },
        {
          OPINION_STATEMENT: [false],
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
  });
});
