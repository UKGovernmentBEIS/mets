import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { ReportingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { AerModule } from '../../../aer.module';
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { MeasurementModule } from '../measurement.module';
import { GasFlowComponent } from './gas-flow.component';

describe('GasFlowComponent', () => {
  let page: Page;
  let router: Router;

  let component: GasFlowComponent;
  let fixture: ComponentFixture<GasFlowComponent>;

  let reportingService: ReportingService;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'MEASUREMENT_CO2',
  });

  class Page extends BasePage<GasFlowComponent> {
    get annualHourlyAverageFlueGasFlow() {
      return this.getInputValue('#annualHourlyAverageFlueGasFlow');
    }
    set annualHourlyAverageFlueGasFlow(value: string) {
      this.setInputValue('#annualHourlyAverageFlueGasFlow', value);
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
    fixture = TestBed.createComponent(GasFlowComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    reportingService = TestBed.inject(ReportingService);

    jest.spyOn(reportingService, 'calculateMeasurementCO2Emissions').mockReturnValue(
      of({
        annualFossilAmountOfGreenhouseGas: '10',
        annualGasFlow: '20',
        globalWarmingPotential: '30',
        reportableEmissions: '40',
        sustainableBiomassEmissions: '50',
      }),
    );

    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, MeasurementModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for adding a new emission point emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              MEASUREMENT_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
                emissionPointEmissions: [
                  {
                    annualHourlyAverageGHGConcentration: '10',
                    biomassPercentages: {
                      biomassPercentage: '20',
                      containsBiomass: true,
                    },
                    operationalHours: '30',
                  },
                ],
              },
            },
          },
          {
            MEASUREMENT_CO2: [],
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
      expect(page.errorSummaryLinks).toEqual(['Enter a value']);

      page.submitButton.click();
      fixture.detectChanges();

      page.annualHourlyAverageFlueGasFlow = '30';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              MEASUREMENT_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
                emissionPointEmissions: [
                  {
                    annualHourlyAverageFlueGasFlow: '30',
                    calculationCorrect: null,
                    annualFossilAmountOfGreenhouseGas: '10',
                    annualGasFlow: '20',
                    annualHourlyAverageGHGConcentration: '10',
                    biomassPercentages: {
                      biomassPercentage: '20',
                      containsBiomass: true,
                    },
                    globalWarmingPotential: '30',
                    operationalHours: '30',
                    reportableEmissions: '40',
                    sustainableBiomassEmissions: '50',
                  },
                ],
              },
            },
          },
          {
            MEASUREMENT_CO2: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../calculation-review'], { relativeTo: route });
    });
  });

  describe('for editing a emission point emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              MEASUREMENT_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
                emissionPointEmissions: [
                  {
                    annualHourlyAverageFlueGasFlow: '30',
                    biomassPercentages: {
                      biomassPercentage: '20',
                      containsBiomass: true,
                    },
                    operationalHours: '30',
                  },
                ],
              },
            },
          },
          {
            MEASUREMENT_CO2: [],
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

      expect(page.annualHourlyAverageFlueGasFlow).toBe('30');
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(2);
      expect(navigateSpy).toHaveBeenCalledWith(['../calculation-review'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
