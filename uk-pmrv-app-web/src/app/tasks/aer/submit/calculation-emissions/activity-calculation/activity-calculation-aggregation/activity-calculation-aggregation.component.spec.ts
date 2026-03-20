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

import { TasksService } from 'pmrv-api';

import { mockAerApplyPayload } from '../../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../../testing/mock-state';
import { CalculationEmissionsModule } from '../../calculation-emissions.module';
import { NationalInventoryService } from '../../services/national-inventory.service';
import { ActivityCalculationAggregationComponent } from './activity-calculation-aggregation.component';

describe('ActivityCalculationAggregationComponent', () => {
  let page: Page;
  let router: Router;

  let component: ActivityCalculationAggregationComponent;
  let fixture: ComponentFixture<ActivityCalculationAggregationComponent>;

  let store: CommonTasksStore;
  let aerService: AerService;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, null);

  const parameterMonitoringTiers = (mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2 as any)
    .sourceStreamEmissions[0].parameterMonitoringTiers;

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

  class Page extends BasePage<ActivityCalculationAggregationComponent> {
    get measurementUnitValue() {
      return this.getInputValue('#measurementUnit');
    }

    set measurementUnitValue(value: string) {
      this.setInputValue('#measurementUnit', value);
    }

    get materialOpeningQuantityValue() {
      return this.getInputValue('#materialOpeningQuantity');
    }

    set materialOpeningQuantityValue(value: string) {
      this.setInputValue('#materialOpeningQuantity', value);
    }

    get materialClosingQuantityValue() {
      return this.getInputValue('#materialClosingQuantity');
    }

    set materialClosingQuantityValue(value: string) {
      this.setInputValue('#materialClosingQuantity', value);
    }

    get materialImportedOrExportedRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="materialImportedOrExported"]');
    }

    get materialImportedQuantityValue() {
      return this.getInputValue('#materialImportedQuantity');
    }

    set materialImportedQuantityValue(value: string) {
      this.setInputValue('#materialImportedQuantity', value);
    }

    get materialExportedQuantityValue() {
      return this.getInputValue('#materialExportedQuantity');
    }

    set materialExportedQuantityValue(value: string) {
      this.setInputValue('#materialExportedQuantity', value);
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
    fixture = TestBed.createComponent(ActivityCalculationAggregationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    aerService = TestBed.inject(AerService);

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
                sourceStreamEmissions: [
                  {
                    parameterCalculationMethod: {
                      fuel: mockFuelName,
                      mainActivitySector: mockSectorName,
                      type: 'NATIONAL_INVENTORY_DATA',
                      calculationActivityDataCalculationMethod: {
                        type: 'CONTINUOUS_METERING',
                      },
                    },
                    parameterMonitoringTiers,
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

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Please select a measurement unit',
        'Please state the opening quantity of fuel',
        'Please state the closing quantity of fuel',
        'Select yes or no',
      ]);

      page.submitButton.click();
      fixture.detectChanges();

      page.measurementUnitValue = 'TONNES';

      page.materialOpeningQuantityValue = '396.11486498888888888';
      page.materialClosingQuantityValue = '377.4220725';
      page.materialImportedOrExportedRadios[0].click();
      page.materialImportedQuantityValue = '1402.695872';
      page.materialExportedQuantityValue = '0';

      fixture.detectChanges();

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
                    parameterCalculationMethod: {
                      fuel: mockFuelName,
                      mainActivitySector: mockSectorName,
                      type: 'NATIONAL_INVENTORY_DATA',
                      calculationActivityDataCalculationMethod: {
                        type: 'CONTINUOUS_METERING',
                        measurementUnit: 'TONNES',
                        materialOpeningQuantity: '396.11486498888888888',
                        materialClosingQuantity: '377.4220725',
                        materialImportedOrExported: true,
                        materialImportedQuantity: '1402.695872',
                        materialExportedQuantity: '0',
                        totalMaterial: '1421.38866448888888888',
                        activityData: '1421.38866448888888888',
                      },
                    },
                    parameterMonitoringTiers,
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

      expect(navigateSpy).toHaveBeenCalledWith(['../inventory-data-review'], { relativeTo: route });
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
                    parameterCalculationMethod: {
                      fuel: mockFuelName,
                      mainActivitySector: mockSectorName,
                      type: 'NATIONAL_INVENTORY_DATA',
                      calculationActivityDataCalculationMethod: {
                        type: 'CONTINUOUS_METERING',
                        materialClosingQuantity: 10,
                        materialExportedQuantity: 0,
                        materialImportedOrExported: true,
                        materialImportedQuantity: 0,
                        materialOpeningQuantity: 30,
                        totalMaterial: 20,
                        measurementUnit: 'TONNES',
                      },
                    },
                    parameterMonitoringTiers,
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2: [],
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

      expect(page.measurementUnitValue).toEqual('TONNES');
      expect(page.materialOpeningQuantityValue).toEqual('30');
      expect(page.materialClosingQuantityValue).toEqual('10');
      expect(page.materialImportedQuantityValue).toEqual('0');
      expect(page.materialExportedQuantityValue).toEqual('0');
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      postTaskSaveSpy.mockReturnValue(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../inventory-data-review'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
