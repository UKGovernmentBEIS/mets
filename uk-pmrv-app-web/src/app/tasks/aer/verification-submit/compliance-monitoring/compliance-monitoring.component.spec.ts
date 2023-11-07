import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { ComplianceMonitoringComponent } from '@tasks/aer/verification-submit/compliance-monitoring/compliance-monitoring.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('ComplianceMonitoringComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: ComplianceMonitoringComponent;
  let fixture: ComponentFixture<ComplianceMonitoringComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ComplianceMonitoringComponent> {
    get accuracyRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="accuracy"]');
    }
    get completenessRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="completeness"]');
    }
    get consistencyRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="consistency"]');
    }
    get comparabilityRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="comparability"]');
    }
    get transparencyRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="transparency"]');
    }
    get integrityRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="integrity"]');
    }
    get integrityReason() {
      return this.getInputValue('#integrityReason');
    }
    set integrityReason(value: string) {
      this.setInputValue('#integrityReason', value);
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
    fixture = TestBed.createComponent(ComplianceMonitoringComponent);
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

  describe('for new reference documents details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          complianceMonitoringReporting: null,
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

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList.length).toEqual(6);

      page.accuracyRadioButtons[0].click();
      page.completenessRadioButtons[0].click();
      page.consistencyRadioButtons[0].click();
      page.comparabilityRadioButtons[0].click();
      page.transparencyRadioButtons[0].click();
      page.integrityRadioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Please provide a reason']);

      page.integrityReason = 'No integrity reason';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            complianceMonitoringReporting: {
              ...mockVerificationApplyPayload.verificationReport.complianceMonitoringReporting,
              integrity: false,
              integrityReason: 'No integrity reason',
            },
          },
          { complianceMonitoringReporting: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing reference documents details', () => {
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
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.integrityRadioButtons[1].click();
      page.integrityReason = 'No integrity reason';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            complianceMonitoringReporting: {
              ...mockVerificationApplyPayload.verificationReport.complianceMonitoringReporting,
              integrity: false,
              integrityReason: 'No integrity reason',
            },
          },
          { complianceMonitoringReporting: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
