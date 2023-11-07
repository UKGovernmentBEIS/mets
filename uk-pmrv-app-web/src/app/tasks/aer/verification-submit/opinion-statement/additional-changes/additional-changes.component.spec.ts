import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AdditionalChangesComponent } from '@tasks/aer/verification-submit/opinion-statement/additional-changes/additional-changes.component';
import {
  additionalChangesNotCoveredDetailsDescription,
  noSelection,
} from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { OpinionStatementModule } from '@tasks/aer/verification-submit/opinion-statement/opinion-statement.module';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('AdditionalChangesComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: AdditionalChangesComponent;
  let fixture: ComponentFixture<AdditionalChangesComponent>;

  const tasksService = mockClass(TasksService);
  const additionalChangesNotCoveredDetailsValue = 'Some additional changes';
  const expectedNextRoute = '../site-visits';

  class Page extends BasePage<AdditionalChangesComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get paragraph(): HTMLParagraphElement {
      return this.query<HTMLHeadingElement>('p');
    }

    get monitoringPlanVersions(): HTMLElement {
      return this.query('app-monitoring-plan-versions');
    }

    get monitoringPlanSummary(): HTMLElement {
      return this.query('app-monitoring-plan-summary-template');
    }

    get additionalChangesNotCoveredRadios(): HTMLInputElement[] {
      return this.queryAll<HTMLInputElement>('input[name$="additionalChangesNotCovered"]');
    }

    get additionalChangesNotCoveredDetails() {
      return this.getInputValue('#additionalChangesNotCoveredDetails');
    }

    set additionalChangesNotCoveredDetails(value: string) {
      this.setInputValue('#additionalChangesNotCoveredDetails', value);
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList(): string[] {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AdditionalChangesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new additional changes details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          opinionStatement: null,
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
      expect(page.heading1.textContent.trim()).toEqual('Additional changes not covered in the operators report');
      expect(page.paragraph).toBeTruthy();
      expect(page.monitoringPlanSummary).toBeTruthy();
      expect(page.additionalChangesNotCoveredRadios).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList.length).toEqual(1);
      expect(page.errorSummaryList).toEqual([noSelection]);
    });

    it(`should submit a valid form and navigate to ${expectedNextRoute}`, () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.additionalChangesNotCoveredRadios[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([additionalChangesNotCoveredDetailsDescription]);

      page.additionalChangesNotCoveredDetails = additionalChangesNotCoveredDetailsValue;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            opinionStatement: {
              additionalChangesNotCovered: true,
              additionalChangesNotCoveredDetails: additionalChangesNotCoveredDetailsValue,
            },
          },
          { opinionStatement: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing additional changes details', () => {
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
      expect(page.heading1.textContent.trim()).toEqual('Additional changes not covered in the operators report');
      expect(page.paragraph).toBeTruthy();
      expect(page.monitoringPlanSummary).toBeTruthy();
      expect(page.additionalChangesNotCoveredRadios).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });

    it(`should submit a valid form and navigate to ${expectedNextRoute}`, () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const additionalChangesNotCoveredDetailsEdited = 'Some additional changes edited';

      page.additionalChangesNotCoveredRadios[0].click();
      page.additionalChangesNotCoveredDetails = '';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([additionalChangesNotCoveredDetailsDescription]);

      page.additionalChangesNotCoveredDetails = additionalChangesNotCoveredDetailsEdited;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            opinionStatement: {
              ...mockVerificationApplyPayload.verificationReport.opinionStatement,
              additionalChangesNotCovered: true,
              additionalChangesNotCoveredDetails: additionalChangesNotCoveredDetailsEdited,
            },
          },
          { opinionStatement: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
