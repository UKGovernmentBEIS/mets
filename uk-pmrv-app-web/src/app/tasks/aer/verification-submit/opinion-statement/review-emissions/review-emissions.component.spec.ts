import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { noSelection } from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { OpinionStatementModule } from '@tasks/aer/verification-submit/opinion-statement/opinion-statement.module';
import { ReviewEmissionsComponent } from '@tasks/aer/verification-submit/opinion-statement/review-emissions/review-emissions.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { MonitoringApproachTypeEmissions, OpinionStatement, TasksService } from 'pmrv-api';

describe('ReviewEmissionsComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: ReviewEmissionsComponent;
  let fixture: ComponentFixture<ReviewEmissionsComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ReviewEmissionsComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get fieldSetLegend(): HTMLLegendElement {
      return this.query<HTMLLegendElement>('#operatorEmissionsAcceptable>legend');
    }

    get summaryListElement(): HTMLElement {
      return this.query('app-aer-task-review');
    }

    get reportableEmissions(): HTMLParagraphElement {
      return this.query<HTMLParagraphElement>('#operatorEmissionsAcceptable p:first-of-type');
    }

    get sustainableBiomass(): HTMLParagraphElement {
      return this.query<HTMLParagraphElement>('#operatorEmissionsAcceptable p:nth-of-type(2)');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    set reportableEmissionsValues(values: string[]) {
      Array.from(
        this.queryAll<HTMLInputElement>('input[id^=monitoringApproachTypeEmissions][id$=reportableEmissions]'),
      ).map((el, index) => this.setInputValue(`#${el.id}`, values[index]));
    }

    set biomassValues(values: string[]) {
      Array.from(
        this.queryAll<HTMLInputElement>('input[id^=monitoringApproachTypeEmissions][id$=sustainableBiomass]'),
      ).map((el, index) => this.setInputValue(`#${el.id}`, values[index]));
    }

    get operatorEmissionsAcceptableRadios(): HTMLInputElement[] {
      return this.queryAll<HTMLInputElement>('input[name$="operatorEmissionsAcceptable"]');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReviewEmissionsComponent);
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

  const setupNewState = () => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockStateBuild({
        opinionStatement: {
          operatorEmissionsAcceptable: null,
          monitoringApproachTypeEmissions: null,
        } as OpinionStatement,
      }),
    );
    createComponent();
  };

  describe('for new review emissions', () => {
    it('should create', () => {
      setupNewState();
      expect(component).toBeTruthy();
    });

    it('should display headings, summary list, inputs and form with 0 errors', fakeAsync(() => {
      setupNewState();
      tick(300);
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1.textContent.trim()).toEqual('Review the emissions reported by the operator');
      expect(page.summaryListElement).toBeTruthy();
      expect(page.fieldSetLegend.textContent.trim()).toEqual('Are the emissions provided by the operator acceptable?');
      expect(page.reportableEmissions.textContent.trim()).toEqual('Reportable emissions: 0 tonnes CO2e');
      expect(page.sustainableBiomass.textContent.trim()).toEqual('Sustainable biomass: 0 tonnes CO2e');
      expect(page.submitButton).toBeTruthy();
    }));

    it('should display error on empty form submit, multiple errors when `no` selected', () => {
      setupNewState();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noSelection]);
      expect(page.errorSummaryListContents.length).toEqual(1);

      page.operatorEmissionsAcceptableRadios[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(20);
    });

    it('should submit a valid form and navigate to `additional-changes`', fakeAsync(() => {
      setupNewState();
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.operatorEmissionsAcceptableRadios[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(20);

      page.reportableEmissionsValues = ['1.11111', '2.22222', '3', '4', '5', '6', '7', '8', '9', '10', '11'];
      page.biomassValues = ['1.11111', '2.22222', '3', '4', '5', '6', '7', '8', '11'];
      tick(300);
      fixture.detectChanges();

      expect(page.reportableEmissions.textContent.trim()).toEqual('Reportable emissions: 66.33333 tonnes CO2e');
      expect(page.sustainableBiomass.textContent.trim()).toEqual('Sustainable biomass: 47.33333 tonnes CO2e');

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            opinionStatement: {
              operatorEmissionsAcceptable: false,
              monitoringApproachTypeEmissions: {
                calculationCombustionEmissions: {
                  reportableEmissions: '1.11111',
                  sustainableBiomass: '1.11111',
                },
                calculationProcessEmissions: {
                  reportableEmissions: '2.22222',
                  sustainableBiomass: '2.22222',
                },
                calculationMassBalanceEmissions: {
                  reportableEmissions: '3',
                  sustainableBiomass: '3',
                },
                calculationTransferredCO2Emissions: {
                  reportableEmissions: '4',
                  sustainableBiomass: '4',
                },
                measurementCO2Emissions: {
                  reportableEmissions: '5',
                  sustainableBiomass: '5',
                },
                measurementTransferredCO2Emissions: {
                  reportableEmissions: '6',
                  sustainableBiomass: '6',
                },
                measurementN2OEmissions: {
                  reportableEmissions: '7',
                  sustainableBiomass: '7',
                },
                measurementTransferredN2OEmissions: {
                  reportableEmissions: '8',
                  sustainableBiomass: '8',
                },
                calculationPFCEmissions: {
                  reportableEmissions: '9',
                },
                inherentCO2Emissions: {
                  reportableEmissions: '10',
                },
                fallbackEmissions: {
                  reportableEmissions: '11',
                  sustainableBiomass: '11',
                },
              } as MonitoringApproachTypeEmissions,
            },
          },
          { opinionStatement: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../additional-changes'], { relativeTo: activatedRoute });
    }));
  });

  const setupExistingState = () => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    createComponent();
  };

  describe('for existing review emissions', () => {
    it('should create', () => {
      setupExistingState();
      expect(component).toBeTruthy();
    });

    it('should display headings, summary list, inputs and form with 0 errors', fakeAsync(() => {
      setupExistingState();
      tick(300);
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1.textContent.trim()).toEqual('Review the emissions reported by the operator');
      expect(page.summaryListElement).toBeTruthy();
      expect(page.fieldSetLegend.textContent.trim()).toEqual('Are the emissions provided by the operator acceptable?');
      expect(page.reportableEmissions.textContent.trim()).toEqual('Reportable emissions: 66.33333 tonnes CO2e');
      expect(page.sustainableBiomass.textContent.trim()).toEqual('Sustainable biomass: 1507.33333 tonnes CO2e');
      expect(page.submitButton).toBeTruthy();
    }));

    it('should submit a valid form and navigate to `additional-changes`', () => {
      setupExistingState();
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith(['../additional-changes'], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to `additional-changes`', fakeAsync(() => {
      setupExistingState();

      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.reportableEmissionsValues = ['1.11111', '2.22222', '3', '4', '5', '6', '7', '8', '9', '10', '11.111111'];
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(1);
      expect(page.errorSummaryListContents[0]).toEqual('Enter a number up to 5 decimal places');

      page.reportableEmissionsValues = ['1.11111', '2.22222', '3', '4', '5', '6', '7', '8', '9', '10', '11'];
      page.biomassValues = ['1.11111', '2.22222', '3', '4', '5', '6', '7', '8', '11'];
      tick(300);
      fixture.detectChanges();

      expect(page.reportableEmissions.textContent.trim()).toEqual('Reportable emissions: 66.33333 tonnes CO2e');
      expect(page.sustainableBiomass.textContent.trim()).toEqual('Sustainable biomass: 47.33333 tonnes CO2e');

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            opinionStatement: {
              ...mockVerificationApplyPayload.verificationReport.opinionStatement,
              operatorEmissionsAcceptable: false,
              monitoringApproachTypeEmissions: {
                calculationCombustionEmissions: {
                  reportableEmissions: '1.11111',
                  sustainableBiomass: '1.11111',
                },
                calculationProcessEmissions: {
                  reportableEmissions: '2.22222',
                  sustainableBiomass: '2.22222',
                },
                calculationMassBalanceEmissions: {
                  reportableEmissions: '3',
                  sustainableBiomass: '3',
                },
                calculationTransferredCO2Emissions: {
                  reportableEmissions: '4',
                  sustainableBiomass: '4',
                },
                measurementCO2Emissions: {
                  reportableEmissions: '5',
                  sustainableBiomass: '5',
                },
                measurementTransferredCO2Emissions: {
                  reportableEmissions: '6',
                  sustainableBiomass: '6',
                },
                measurementN2OEmissions: {
                  reportableEmissions: '7',
                  sustainableBiomass: '7',
                },
                measurementTransferredN2OEmissions: {
                  reportableEmissions: '8',
                  sustainableBiomass: '8',
                },
                calculationPFCEmissions: {
                  reportableEmissions: '9',
                },
                inherentCO2Emissions: {
                  reportableEmissions: '10',
                },
                fallbackEmissions: {
                  reportableEmissions: '11',
                  sustainableBiomass: '11',
                },
              } as MonitoringApproachTypeEmissions,
            },
          },
          { opinionStatement: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../additional-changes'], { relativeTo: activatedRoute });
    }));
  });
});
