import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockPostBuild, mockStateBuild } from '@tasks/vir/submit/testing/mock-state';
import {
  mockState,
  mockVirApplicationSubmitPayload,
} from '@tasks/vir/submit/testing/mock-vir-application-submit-payload';
import { VirTaskSubmitModule } from '@tasks/vir/submit/vir-task-submit.module';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskAttachmentsHandlingService, TasksService, UncorrectedItem } from 'pmrv-api';

import { UploadEvidenceFilesComponent } from './upload-evidence-files.component';

describe('UploadEvidenceFilesComponent', () => {
  let page: Page;
  let router: Router;
  let control: FormControl;
  let store: CommonTasksStore;
  let component: UploadEvidenceFilesComponent;
  let fixture: ComponentFixture<UploadEvidenceFilesComponent>;

  const currentItem: UncorrectedItem = mockVirApplicationSubmitPayload.verificationData.uncorrectedNonConformities.B1;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockState.requestTaskItem.requestTask.id, id: currentItem.reference },
    null,
    {
      verificationDataItem: currentItem,
    },
  );
  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const expectedNextRoute = '../../B1/summary';
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
      imports: [VirTaskSubmitModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  describe('for new upload evidence files details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          operatorImprovementResponses: {},
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

    it('should submit a valid form and navigate to `summary` page', async () => {
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
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            operatorImprovementResponses: {
              B1: {
                files: [uuid1, uuid2],
              },
            },
          },
          { B1: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing upload evidence files details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
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
      expect(page.fileDeleteButtons).toHaveLength(3);
      expect(page.submitButton).toBeTruthy();
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: uuid2 } })),
      );

      page.fileDeleteButtons[2].click();
      page.filesValue = [new File(['test content 2'], 'testfile2.jpg')];
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            operatorImprovementResponses: {
              ...mockVirApplicationSubmitPayload.operatorImprovementResponses,
              [currentItem.reference]: {
                ...mockVirApplicationSubmitPayload.operatorImprovementResponses.B1,
                files: [
                  mockVirApplicationSubmitPayload.operatorImprovementResponses.B1.files[0],
                  mockVirApplicationSubmitPayload.operatorImprovementResponses.B1.files[1],
                  uuid2,
                ],
              },
            },
          },
          { B1: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../../B1/summary'], { relativeTo: activatedRoute });
    });
  });
});
