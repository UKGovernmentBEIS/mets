import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { ReportingService, TasksService } from 'pmrv-api';

import { mockAerApplyPayload } from '../../../testing/mock-aer-apply-action';
import { mockStateBuild } from '../../../testing/mock-state';
import { CalculationEmissionsModule } from '../../calculation-emissions.module';
import { NationalInventoryService } from '../../services/national-inventory.service';
import { InventoryDataReviewComponent } from './inventory-data-review.component';

describe('InventoryDataReviewComponent', () => {
  let page: Page;
  let router: Router;

  let component: InventoryDataReviewComponent;
  let fixture: ComponentFixture<InventoryDataReviewComponent>;

  let store: CommonTasksStore;
  let aerService: AerService;
  let reportingService: ReportingService;

  const tasksService = mockClass(TasksService);

  const route = new ActivatedRouteStub({ index: '0' }, null, null);

  const emissionCalculationParamValues = {
    emissionFactor: '32',
    oxidationFactor: '3',
    efMeasurementUnit: 'TONNES_OF_CO2_PER_TJ',
    netCalorificValue: '4',
    ncvMeasurementUnit: 'GJ_PER_TONNE',
    totalReportableEmissions: '1.47456',
    totalSustainableBiomassEmissions: '0.06144',
  };

  const sourceStreamEmission = {
    parameterCalculationMethod: {
      fuel: 'LPG',
      type: 'NATIONAL_INVENTORY_DATA',
      mainActivitySector: '1A1b',
      emissionCalculationParamValues: emissionCalculationParamValues,
      calculationActivityDataCalculationMethod: {
        type: 'CONTINUOUS_METERING',
        totalMaterial: '4',
        measurementUnit: 'TONNES',
      },
    },
    parameterMonitoringTierDiffReason: {
      type: 'DATA_GAP',
      reason: 'cvxcvx',
      relatedNotifications: ['AEMN1-1'],
    },
  };

  const mockSectorName = '1A1a';
  const mockFuelName = 'Coal';
  const nationalInventoryService = {
    nationalInventoryData$: of({
      sectors: [
        {
          name: mockSectorName,
          displayName: 'Public Electricity & Heat Production',
          fuels: [
            {
              name: mockFuelName,
            },
          ],
        },
      ],
    }),
  };

  const reportableEmissions = {
    reportableEmissions: emissionCalculationParamValues.totalReportableEmissions,
    sustainableBiomassEmissions: emissionCalculationParamValues.totalSustainableBiomassEmissions,
  };
  class Page extends BasePage<InventoryDataReviewComponent> {
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
    fixture = TestBed.createComponent(InventoryDataReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    aerService = TestBed.inject(AerService);

    reportingService = TestBed.inject(ReportingService);
    jest.spyOn(reportingService, 'calculateEmissions').mockReturnValue(of(reportableEmissions));

    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, CalculationEmissionsModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: NationalInventoryService, useValue: nationalInventoryService },
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
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      postTaskSaveSpy.mockReturnValue(of({}));

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
      expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
      expect(postTaskSaveSpy).toHaveBeenCalledWith(
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
        undefined,
        [false],
        'CALCULATION_CO2',
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route });
    });
  });

  describe('for editing a source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
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

      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      postTaskSaveSpy.mockReturnValue(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
