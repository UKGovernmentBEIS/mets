import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { InspectionModule } from '@tasks/inspection/inspection.module';
import {
  inspectionForSubmitMockPostBuild,
  inspectionSubmitMockState,
  mockInspectionSubmitRequestTaskPayload,
} from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { FollowUpActionsGuardQuestionComponent } from './follow-up-action-guard-question.component';

describe('FollowUpActionsGuardQuestionComponent', () => {
  let page: Page;
  let router: Router;

  let store: CommonTasksStore;
  let component: FollowUpActionsGuardQuestionComponent;
  let fixture: ComponentFixture<FollowUpActionsGuardQuestionComponent>;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'audit', id: '0' });

  class Page extends BasePage<FollowUpActionsGuardQuestionComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get followUpActionsRequired() {
      return this.queryAll<HTMLInputElement>('input[name$="followUpActionsRequired"]');
    }

    get followUpActionsOmissionJustification(): HTMLElement {
      return this.query('#followUpActionsOmissionJustification');
    }

    set followUpActionsOmissionJustification(value: string) {
      this.setInputValue('#followUpActionsOmissionJustification', value);
    }

    get multipleFileInput(): HTMLElement {
      return this.query('app-multiple-file-input');
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
    fixture = TestBed.createComponent(FollowUpActionsGuardQuestionComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InspectionModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new followup', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(inspectionSubmitMockState);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Do you want to add follow-up actions for the operator?');
      expect(page.followUpActionsRequired).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display conditional HTMLElements', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Do you want to add follow-up actions for the operator?');
      expect(page.followUpActionsRequired).toBeTruthy();
      page.followUpActionsRequired[1].click();
      fixture.detectChanges();
      expect(page.followUpActionsOmissionJustification).toBeTruthy();
      expect(page.multipleFileInput).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to next page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.followUpActionsRequired[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        inspectionForSubmitMockPostBuild(
          {
            installationInspection: {
              ...mockInspectionSubmitRequestTaskPayload.installationInspection,
              followUpActionsRequired: true,
              followUpActionsOmissionFiles: [],
              followUpActionsOmissionJustification: null,
            },
          },
          { followUpAction: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../follow-up-summary'], { relativeTo: activatedRoute });
    });
  });
});
