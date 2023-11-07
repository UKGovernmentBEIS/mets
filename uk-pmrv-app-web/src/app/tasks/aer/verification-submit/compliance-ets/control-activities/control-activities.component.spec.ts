import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ComplianceEtsModule } from '@tasks/aer/verification-submit/compliance-ets/compliance-ets.module';
import { ControlActivitiesComponent } from '@tasks/aer/verification-submit/compliance-ets/control-activities/control-activities.component';
import { noReason } from '@tasks/aer/verification-submit/compliance-ets/errors/compliance-ets-validation.errors';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('ControlActivitiesComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: ControlActivitiesComponent;
  let fixture: ComponentFixture<ControlActivitiesComponent>;

  const tasksService = mockClass(TasksService);
  const controlActivitiesNotDocumentedReasonValue = 'No control activities reason';
  const nextRoute = '../monitoring-plan-procedures';

  class Page extends BasePage<ControlActivitiesComponent> {
    get controlActivitiesDocumentedButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="controlActivitiesDocumented"]');
    }

    get controlActivitiesNotDocumentedReason() {
      return this.getInputValue('#controlActivitiesNotDocumentedReason');
    }

    set controlActivitiesNotDocumentedReason(value: string) {
      this.setInputValue('#controlActivitiesNotDocumentedReason', value);
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
    fixture = TestBed.createComponent(ControlActivitiesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComplianceEtsModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new control activities details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          etsComplianceRules: null,
        }),
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
    });

    it(`should submit a valid form and navigate to ${nextRoute}`, () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList.length).toEqual(1);

      page.controlActivitiesDocumentedButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([noReason]);

      page.controlActivitiesNotDocumentedReason = controlActivitiesNotDocumentedReasonValue;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            etsComplianceRules: {
              controlActivitiesDocumented: false,
              controlActivitiesNotDocumentedReason: controlActivitiesNotDocumentedReasonValue,
            },
          },
          { etsComplianceRules: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([nextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing control activities details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it(`should submit a valid form and navigate to ${nextRoute}`, () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith([nextRoute], { relativeTo: activatedRoute });
    });

    it(`should edit, submit a valid form and navigate to ${nextRoute}`, () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.controlActivitiesDocumentedButtons[1].click();
      page.controlActivitiesNotDocumentedReason = controlActivitiesNotDocumentedReasonValue;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            etsComplianceRules: {
              ...mockVerificationApplyPayload.verificationReport.etsComplianceRules,
              controlActivitiesDocumented: false,
              controlActivitiesNotDocumentedReason: controlActivitiesNotDocumentedReasonValue,
            },
          },
          { etsComplianceRules: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([nextRoute], { relativeTo: activatedRoute });
    });
  });
});
