import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import {
  MeasurementOfCO2EmissionPointCategoryAppliedTier,
  MeasurementOfN2OEmissionPointCategoryAppliedTier,
  TasksService,
} from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { MeasurementModule } from '../../approaches/measurement/measurement.module';
import { N2oModule } from '../../approaches/n2o/n2o.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { EmissionsComponent } from './emissions.component';

describe('EmissionsComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: EmissionsComponent;
  let fixture: ComponentFixture<EmissionsComponent>;
  let page: Page;
  let router: Router;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<EmissionsComponent> {
    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get ulists() {
      return this.queryAll<HTMLUListElement>('ul > li');
    }

    get measurementDevicesOrMethods() {
      return this.fixture.componentInstance.form.get('measurementDevicesOrMethods').value;
    }
    get samplingFrequencyMonthlyOption() {
      return this.query<HTMLInputElement>('#samplingFrequency-option3');
    }

    get samplingFrequencyAnnuallyOption() {
      return this.query<HTMLInputElement>('#samplingFrequency-option5');
    }

    get tier2Option() {
      return this.query<HTMLInputElement>('#tier-option2');
    }

    get tier0Option() {
      return this.query<HTMLInputElement>('#tier-option0');
    }

    get isHighestTierRequiredOption() {
      return this.query<HTMLInputElement>('#isHighestRequiredTierT1-option1');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(EmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  const taskKeys = ['MEASUREMENT_N2O', 'MEASUREMENT_CO2'];

  for (const taskKey of taskKeys) {
    describe(taskKey, () => {
      const route = new ActivatedRouteStub(
        {},
        {},
        {
          taskKey: taskKey,
        },
      );

      beforeEach(async () => {
        await TestBed.configureTestingModule({
          imports: [RouterTestingModule, SharedModule, MeasurementModule, N2oModule],
          providers: [
            { provide: ActivatedRoute, useValue: route },
            { provide: TasksService, useValue: tasksService },
            {
              provide: PermitApplicationStore,
              useExisting: PermitIssuanceStore,
            },
          ],
        }).compileComponents();
      });

      describe('prerequisites not completed', () => {
        beforeEach(() => {
          store = TestBed.inject(PermitApplicationStore);
          store.setState(
            mockStateBuild(
              {
                measurementDevicesOrMethods: [],
                monitoringApproaches: {
                  [taskKey]: {
                    emissionPointCategoryAppliedTiers: [],
                  },
                },
              },
              {
                sourceStreams: [false],
                [`${taskKey}_Category`]: [false],
                [`${taskKey}_Measured_Emissions`]: [false],
              },
            ),
          );
        });

        beforeEach(() => {
          createComponent();
        });

        it('should create', () => {
          expect(component).toBeTruthy();
        });

        it('should display the not started page ', () => {
          expect(
            page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
          ).toBeTruthy();

          expect(page.ulists.find((ul) => ul.textContent.trim() === 'Emission point category')).toBeTruthy();
          expect(page.ulists.find((ul) => ul.textContent.trim() === 'Measurement devices or methods')).toBeTruthy();
        });
      });

      describe('measured emissions not completed and prerequisities completed', () => {
        beforeEach(() => {
          store = TestBed.inject(PermitApplicationStore);
          store.setState(
            mockStateBuild(
              {
                monitoringApproaches: {
                  [taskKey]: {
                    type: taskKey,
                    emissionPointCategoryAppliedTiers: [
                      {
                        emissionPointCategory: {
                          sourceStreams: ['16236817394240.1574963093314663'],
                          emissionSources: ['16245246343280.27155194483385103'],
                          emissionPoint: '16363790610230.8369404469603225',
                          emissionType: 'ABATED',
                          monitoringApproachType: 'CALCULATION',
                          annualEmittedCO2Tonnes: '23.5',
                          categoryType: 'MAJOR',
                        },
                      },
                    ] as
                      | MeasurementOfCO2EmissionPointCategoryAppliedTier[]
                      | MeasurementOfN2OEmissionPointCategoryAppliedTier[],
                  },
                },
              },
              {
                measurementDevicesOrMethods: [true],
                [`${taskKey}_Category`]: [true],
                [`${taskKey}_Measured_Emissions`]: [false],
              },
            ),
          );
        });

        beforeEach(() => {
          createComponent();
        });

        it('should create', () => {
          expect(component).toBeTruthy();
        });

        it('should NOT display the not started page ', () => {
          expect(
            page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
          ).toBeFalsy();

          expect(page.ulists.find((ul) => ul.textContent.trim() === 'Emission point category')).toBeFalsy();
          expect(page.ulists.find((ul) => ul.textContent.trim() === 'Measurement devices or methods')).toBeFalsy();
        });
      });

      describe('for measured emissions with justification', () => {
        beforeEach(() => {
          store = TestBed.inject(PermitApplicationStore);
          store.setState(
            mockStateBuild(
              {
                monitoringApproaches: {
                  [taskKey]: {
                    type: taskKey,
                    emissionPointCategoryAppliedTiers: [
                      {
                        emissionPointCategory: {
                          sourceStreams: ['16236817394240.1574963093314663'],
                          emissionSources: ['16245246343280.27155194483385103'],
                          emissionPoint: '16363790610230.8369404469603225',
                          emissionType: 'ABATED',
                          monitoringApproachType: 'CALCULATION',
                          annualEmittedCO2Tonnes: '23.5',
                          categoryType: 'MAJOR',
                        },
                        measuredEmissions: {
                          measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                          samplingFrequency: 'MONTHLY',
                          tier: 'TIER_1',
                          isHighestRequiredTier: false,
                        },
                      },
                    ] as
                      | MeasurementOfCO2EmissionPointCategoryAppliedTier[]
                      | MeasurementOfN2OEmissionPointCategoryAppliedTier[],
                  },
                },
              },
              {
                measurementDevicesOrMethods: [true],
                [`${taskKey}_Category`]: [true],
                [`${taskKey}_Measured_Emissions`]: [false],
              },
            ),
          );
        });

        beforeEach(createComponent);

        it('should create', () => {
          expect(component).toBeTruthy();
        });

        it('should fill form from store', () => {
          expect(page.samplingFrequencyMonthlyOption.value).toEqual('MONTHLY');
          expect(page.measurementDevicesOrMethods).toEqual(['16236817394240.1574963093314700']);
          expect(page.tier2Option.value).toEqual(taskKey === 'MEASUREMENT_CO2' ? 'TIER_2' : 'TIER_1');
          expect(page.isHighestTierRequiredOption.value).toEqual('false');
        });

        it('should submit updated values and navigate to justification', () => {
          tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

          const navigateSpy = jest.spyOn(router, 'navigate');

          page.samplingFrequencyAnnuallyOption.click();
          fixture.detectChanges();

          page.submitButton.click();
          fixture.detectChanges();

          expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
          expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
            mockPostBuild(
              {
                monitoringApproaches: {
                  ...store.getState().permit.monitoringApproaches,
                  [taskKey]: {
                    ...store.getState().permit.monitoringApproaches[taskKey],
                    emissionPointCategoryAppliedTiers: [
                      {
                        emissionPointCategory: {
                          sourceStreams: ['16236817394240.1574963093314663'],
                          emissionSources: ['16245246343280.27155194483385103'],
                          emissionPoint: '16363790610230.8369404469603225',
                          emissionType: 'ABATED',
                          monitoringApproachType: 'CALCULATION',
                          annualEmittedCO2Tonnes: '23.5',
                          categoryType: 'MAJOR',
                        },
                        measuredEmissions: {
                          measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                          samplingFrequency: 'ANNUALLY',
                          tier: 'TIER_1',
                          isHighestRequiredTier: false,
                        },
                      },
                    ] as
                      | MeasurementOfCO2EmissionPointCategoryAppliedTier[]
                      | MeasurementOfN2OEmissionPointCategoryAppliedTier[],
                  },
                },
              },
              { ...store.getState().permitSectionsCompleted, [`${taskKey}_Measured_Emissions`]: [false] },
            ),
          );
          expect(navigateSpy).toHaveBeenCalledWith(['justification'], {
            relativeTo: route,
          });
        });

        it('should not post if not dirty', () => {
          const navigateSpy = jest.spyOn(router, 'navigate');

          page.submitButton.click();

          expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
          expect(navigateSpy).toHaveBeenCalledTimes(1);
          expect(navigateSpy).toHaveBeenCalledWith(['justification'], { relativeTo: route });
        });
      });

      describe('for measured emissions without justification', () => {
        beforeEach(() => {
          store = TestBed.inject(PermitApplicationStore);
          store.setState(
            mockStateBuild(
              {
                monitoringApproaches: {
                  [taskKey]: {
                    type: taskKey,
                    emissionPointCategoryAppliedTiers: [
                      {
                        emissionPointCategory: {
                          sourceStreams: ['16236817394240.1574963093314663'],
                          emissionSources: ['16245246343280.27155194483385103'],
                          emissionPoint: '16363790610230.8369404469603225',
                          emissionType: 'ABATED',
                          monitoringApproachType: 'CALCULATION',
                          annualEmittedCO2Tonnes: '23.5',
                          categoryType: 'MAJOR',
                        },
                        measuredEmissions: {
                          measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                          samplingFrequency: 'MONTHLY',
                          tier: taskKey === 'MEASUREMENT_CO2' ? 'TIER_4' : 'TIER_3',
                        },
                      },
                    ] as
                      | MeasurementOfCO2EmissionPointCategoryAppliedTier[]
                      | MeasurementOfN2OEmissionPointCategoryAppliedTier[],
                  },
                },
              },
              {
                measurementDevicesOrMethods: [true],
                [`${taskKey}_Category`]: [true],
                [`${taskKey}_Measured_Emissions`]: [false],
              },
            ),
          );
        });

        beforeEach(createComponent);

        it('should create', () => {
          expect(component).toBeTruthy();
        });

        it('should fill form from store', () => {
          expect(page.samplingFrequencyMonthlyOption.value).toEqual('MONTHLY');
          expect(page.measurementDevicesOrMethods).toEqual(['16236817394240.1574963093314700']);
          expect(page.tier0Option.value).toEqual(taskKey === 'MEASUREMENT_CO2' ? 'TIER_4' : 'TIER_3');
        });

        it('should submit updated values and navigate to answers', () => {
          tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

          const navigateSpy = jest.spyOn(router, 'navigate');

          page.samplingFrequencyAnnuallyOption.click();
          fixture.detectChanges();

          page.submitButton.click();
          fixture.detectChanges();

          expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
          expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
            mockPostBuild(
              {
                monitoringApproaches: {
                  ...store.getState().permit.monitoringApproaches,
                  [taskKey]: {
                    ...store.getState().permit.monitoringApproaches[taskKey],
                    emissionPointCategoryAppliedTiers: [
                      {
                        emissionPointCategory: {
                          sourceStreams: ['16236817394240.1574963093314663'],
                          emissionSources: ['16245246343280.27155194483385103'],
                          emissionPoint: '16363790610230.8369404469603225',
                          emissionType: 'ABATED',
                          monitoringApproachType: 'CALCULATION',
                          annualEmittedCO2Tonnes: '23.5',
                          categoryType: 'MAJOR',
                        },
                        measuredEmissions: {
                          measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                          samplingFrequency: 'ANNUALLY',
                          tier: taskKey === 'MEASUREMENT_CO2' ? 'TIER_4' : 'TIER_3',
                        },
                      },
                    ] as
                      | MeasurementOfCO2EmissionPointCategoryAppliedTier[]
                      | MeasurementOfN2OEmissionPointCategoryAppliedTier[],
                  },
                },
              },
              { ...store.getState().permitSectionsCompleted, [`${taskKey}_Measured_Emissions`]: [false] },
            ),
          );
          expect(navigateSpy).toHaveBeenCalledWith(['justification'], {
            relativeTo: route,
          });
        });
      });
    });
  }
});
