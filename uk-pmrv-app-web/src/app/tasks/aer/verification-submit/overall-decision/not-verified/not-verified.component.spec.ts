import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { NotVerifiedComponent } from '@tasks/aer/verification-submit/overall-decision/not-verified/not-verified.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('NotVerifiedComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: NotVerifiedComponent;
  let fixture: ComponentFixture<NotVerifiedComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<NotVerifiedComponent> {
    get checkboxes() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }
    get checkbox_labels() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__label');
    }
    get otherReason() {
      return this.getInputValue('#otherReason');
    }
    set otherReason(value: string) {
      this.setInputValue('#otherReason', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(NotVerifiedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new overall decision', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          overallAssessment: {
            type: 'NOT_VERIFIED',
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a valid form and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.checkboxes.length).toEqual(7);
      expect(page.checkbox_labels[0].innerHTML).toContain(
        'uncorrected material mis-statment (individual or in aggregate)',
      );
      expect(page.checkbox_labels[1].innerHTML).toContain(
        'uncorrected material non-comformity (individual or in aggregate)',
      );
      expect(page.checkbox_labels[2].innerHTML).toContain(
        'limitations in the data or information made available for verification',
      );
      expect(page.checkbox_labels[3].innerHTML).toContain('limitations of scope due to lack of clarity');
      expect(page.checkbox_labels[4].innerHTML).toContain(
        'the monitoring plan is not approved by the competent authority',
      );
      expect(page.checkbox_labels[5].innerHTML).toContain('limitations of scope of the approved monitoring plan');
      expect(page.checkbox_labels[6].innerHTML).toContain('some other reason');

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select a reason']);

      page.checkboxes[5].click();
      page.checkboxes[6].click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select a reason']);

      page.otherReason = 'Add other reason';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            overallAssessment: {
              type: 'NOT_VERIFIED',
              notVerifiedReasons: [
                {
                  type: 'SCOPE_LIMITATIONS_MONITORING_PLAN',
                },
                {
                  type: 'ANOTHER_REASON',
                  otherReason: 'Add other reason',
                },
              ],
            },
          },
          { overallAssessment: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing overall decision', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.checkboxes[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            overallAssessment: {
              type: 'NOT_VERIFIED',
              notVerifiedReasons: [
                {
                  type: 'UNCORRECTED_MATERIAL_MISSTATEMENT',
                },
                {
                  type: 'NOT_APPROVED_MONITORING_PLAN',
                },
              ],
            },
          },
          { overallAssessment: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
    });
  });
});
