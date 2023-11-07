import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../../store/common-tasks.store';
import { AerModule } from '../../../../aer.module';
import { mockAerApplyPayload, mockState } from '../../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../../testing/mock-state';
import { CalculationEmissionsModule } from '../../calculation-emissions.module';
import { ManualDataReviewComponent } from './manual-data-review.component';

describe('ManualDataReviewComponent', () => {
  let page: Page;
  let router: Router;

  let component: ManualDataReviewComponent;
  let fixture: ComponentFixture<ManualDataReviewComponent>;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, null);

  const sourceStreamEmission = {
    parameterCalculationMethod: {
      type: 'MANUAL',
      emissionCalculationParamValues: {
        emissionFactor: '32',
        oxidationFactor: '3',
        efMeasurementUnit: 'TONNES_OF_CO2_PER_TJ',
        netCalorificValue: '4',
        ncvMeasurementUnit: 'GJ_PER_TONNE',
        totalReportableEmissions: '1.47456',
        totalSustainableBiomassEmissions: '0.06144',
      },
      calculationActivityDataCalculationMethod: {
        type: 'CONTINUOUS_METERING',
        totalMaterial: 4,
        measurementUnit: 'TONNES',
      },
    },
    parameterMonitoringTierDiffReason: {
      type: 'DATA_GAP',
      reason: 'cvxcvx',
      relatedNotifications: ['AEMN1-1'],
    },
  };
  class Page extends BasePage<ManualDataReviewComponent> {
    get calculationCorrectRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="calculationCorrect"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ManualDataReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, CalculationEmissionsModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for adding a new source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
                sourceStreamEmissions: [sourceStreamEmission],
              },
            },
          },
          {
            CALCULATION_CO2: [false],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select yes if the calculated emissions are correct']);

      page.submitButton.click();
      fixture.detectChanges();

      page.calculationCorrectRadios[0].click();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
                sourceStreamEmissions: [
                  {
                    ...sourceStreamEmission,
                    parameterCalculationMethod: {
                      ...sourceStreamEmission.parameterCalculationMethod,
                      emissionCalculationParamValues: {
                        ...sourceStreamEmission.parameterCalculationMethod.emissionCalculationParamValues,
                        calculationCorrect: true,
                        providedEmissions: null,
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route });
    });
  });

  describe('for editing a source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should  fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.calculationCorrectRadios.length).toEqual(2);
      expect(page.calculationCorrectRadios[0].checked).toBeTruthy();
      expect(page.calculationCorrectRadios[1].checked).toBeFalsy();
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      component.form.markAsDirty();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
