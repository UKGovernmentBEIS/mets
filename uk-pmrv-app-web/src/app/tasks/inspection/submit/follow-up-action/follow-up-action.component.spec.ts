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

import { FollowUpActionSubmitComponent } from './follow-up-action.component';

describe('FollowUpActionComponent', () => {
  let page: Page;
  let router: Router;

  let store: CommonTasksStore;
  let component: FollowUpActionSubmitComponent;
  let fixture: ComponentFixture<FollowUpActionSubmitComponent>;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'audit', id: '0' });

  class Page extends BasePage<FollowUpActionSubmitComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    set followUpActionType(value: string) {
      this.setInputValue('#followUpActionType', value);
    }

    set explanation(value: string) {
      this.setInputValue('#explanation', value);
    }

    set filesValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }

    get multipleFileInput(): HTMLElement {
      return this.query('app-multiple-file-input');
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
    fixture = TestBed.createComponent(FollowUpActionSubmitComponent);
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

  describe('for existing followup action', () => {
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
      expect(page.heading1.textContent.trim()).toEqual('Add a follow-up action');
      expect(page.multipleFileInput).toBeTruthy();
      expect(page.fileDeleteButtons).toHaveLength(1);
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.explanation = mockInspectionSubmitRequestTaskPayload.installationInspection.followUpActions[0].explanation;

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        inspectionForSubmitMockPostBuild(
          {
            installationInspection: {
              ...mockInspectionSubmitRequestTaskPayload.installationInspection,
            },
          },
          { followUpAction: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../../follow-up-actions'], { relativeTo: activatedRoute });
    });
  });
});
