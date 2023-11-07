import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { noSelection } from '@tasks/vir/errors/validation-errors';
import { mockPostBuild, mockStateBuild } from '@tasks/vir/submit/testing/mock-state';
import {
  mockState,
  mockVirApplicationSubmitPayload,
} from '@tasks/vir/submit/testing/mock-vir-application-submit-payload';
import { UploadEvidenceQuestionComponent } from '@tasks/vir/submit/upload-evidence-question/upload-evidence-question.component';
import { VirTaskSubmitModule } from '@tasks/vir/submit/vir-task-submit.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService, UncorrectedItem } from 'pmrv-api';

describe('UploadEvidenceQuestionComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: UploadEvidenceQuestionComponent;
  let fixture: ComponentFixture<UploadEvidenceQuestionComponent>;

  const currentItem: UncorrectedItem = mockVirApplicationSubmitPayload.verificationData.uncorrectedNonConformities.B1;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockState.requestTaskItem.requestTask.id, id: currentItem.reference },
    null,
    {
      verificationDataItem: currentItem,
    },
  );
  const tasksService = mockClass(TasksService);
  const expectedNextRoute = '../../B1/upload-evidence-files';

  class Page extends BasePage<UploadEvidenceQuestionComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get uploadEvidenceButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="uploadEvidence"]');
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
    fixture = TestBed.createComponent(UploadEvidenceQuestionComponent);
    component = fixture.componentInstance;
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
      ],
    }).compileComponents();
  });

  describe('for new upload evidence question details', () => {
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
      expect(page.heading1.textContent.trim()).toEqual('Would you like to upload evidence to support your response?');

      expect(page.uploadEvidenceButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noSelection]);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to `upload-evidence-files` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.uploadEvidenceButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            operatorImprovementResponses: {
              B1: {
                uploadEvidence: true,
                files: [],
              },
            },
          },
          { B1: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing upload evidence question details', () => {
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
      expect(page.heading1.textContent.trim()).toEqual('Would you like to upload evidence to support your response?');

      expect(page.uploadEvidenceButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should submit a valid form and navigate to `upload-evidence-files` page', () => {
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

      page.uploadEvidenceButtons[1].click();
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
                uploadEvidence: false,
                files: [],
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
