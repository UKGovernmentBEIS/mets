import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ComplianceEtsModule } from '@tasks/aer/verification-submit/compliance-ets/compliance-ets.module';
import { noReason } from '@tasks/aer/verification-submit/compliance-ets/errors/compliance-ets-validation.errors';
import { RegulationMonitoringReportingComponent } from '@tasks/aer/verification-submit/compliance-ets/regulation-monitoring-reporting/regulation-monitoring-reporting.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('RegulationMonitoringReportingComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: RegulationMonitoringReportingComponent;
  let fixture: ComponentFixture<RegulationMonitoringReportingComponent>;

  const tasksService = mockClass(TasksService);
  const euRegulationMonitoringReportingNotMetReasonValue = 'No regulation monitoring reporting reason';
  const nextRoute = '../detail-source-data';

  class Page extends BasePage<RegulationMonitoringReportingComponent> {
    get euRegulationMonitoringReportingMetButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="euRegulationMonitoringReportingMet"]');
    }

    get euRegulationMonitoringReportingNotMetReason() {
      return this.getInputValue('#euRegulationMonitoringReportingNotMetReason');
    }

    set euRegulationMonitoringReportingNotMetReason(value: string) {
      this.setInputValue('#euRegulationMonitoringReportingNotMetReason', value);
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
    fixture = TestBed.createComponent(RegulationMonitoringReportingComponent);
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

  describe('for new regulation monitoring reporting details', () => {
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

      page.euRegulationMonitoringReportingMetButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([noReason]);

      page.euRegulationMonitoringReportingNotMetReason = euRegulationMonitoringReportingNotMetReasonValue;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            etsComplianceRules: {
              euRegulationMonitoringReportingMet: false,
              euRegulationMonitoringReportingNotMetReason: euRegulationMonitoringReportingNotMetReasonValue,
            },
          },
          { etsComplianceRules: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([nextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing regulation monitoring reporting details', () => {
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

      page.euRegulationMonitoringReportingMetButtons[1].click();
      page.euRegulationMonitoringReportingNotMetReason = euRegulationMonitoringReportingNotMetReasonValue;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            etsComplianceRules: {
              ...mockVerificationApplyPayload.verificationReport.etsComplianceRules,
              euRegulationMonitoringReportingMet: false,
              euRegulationMonitoringReportingNotMetReason: euRegulationMonitoringReportingNotMetReasonValue,
            },
          },
          { etsComplianceRules: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([nextRoute], { relativeTo: activatedRoute });
    });
  });
});
