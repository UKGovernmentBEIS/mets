import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { mockPostBuild, mockStateBuild } from '@tasks/hseti/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { HSETIDetailsComponent } from './hseti-details.component';

describe('HSETIDetailsComponent', () => {
  let page: Page;
  let router: Router;
  let control: FormControl;
  let hseTiFileControl: FormControl;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: HSETIDetailsComponent;
  let fixture: ComponentFixture<HSETIDetailsComponent>;

  const expectedNextRoute = 'summary';
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const uuid1 = '11111111-1111-4111-a111-111111111111';
  const uuid2 = '22222222-2222-4222-a222-222222222222';

  class Page extends BasePage<HSETIDetailsComponent> {
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
    fixture = TestBed.createComponent(HSETIDetailsComponent);
    component = fixture.componentInstance;
    control = component.form.get('files') as FormControl;
    hseTiFileControl = component.form.get('hseTiFile') as FormControl;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule],
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
      store.setState(
        mockStateBuild({
          hseti: {},
        }),
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Upload the 2021-2025 HSE target increase file');
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

    it('should submit a valid form and navigate to next page', async () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: uuid2 } })),
      );

      hseTiFileControl.setValue({
        file: new File(['test hseTi file content 1'], 'hseTifile-testfile1.jpg'),
        uuid: uuid2,
      });
      control.setValue([{ file: new File(['test content 1'], 'testfile1.jpg'), uuid: uuid1 }]);
      page.filesValue = [new File(['test content 2'], 'testfile2.jpg')];
      fixture.detectChanges();

      expect(page.fileDeleteButtons).toHaveLength(2);
      expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['testfile2.jpg', 'testfile1.jpg']);

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            hseti: {
              hsetiFile: uuid2,
              files: ['11111111-1111-4111-a111-111111111111'],
              notes: null,
            },
          },
          {
            details: false,
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
