import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import {
  MeasurementOfCO2EmissionPointCategoryAppliedTier,
  MeasurementOfN2OEmissionPointCategoryAppliedTier,
} from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../shared/shared.module';
import { MeasurementModule } from '../../../approaches/measurement/measurement.module';
import { N2oModule } from '../../../approaches/n2o/n2o.module';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockStateBuild } from '../../../testing/mock-state';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  class Page extends BasePage<SummaryComponent> {
    get measuredEmissionsProperties() {
      return this.queryAll<HTMLDListElement>('dl > div > dd');
    }

    get changeLink() {
      return this.query<HTMLLinkElement>('h2.govuk-heading-m > a');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  const taskKeys = ['MEASUREMENT_N2O', 'MEASUREMENT_CO2'];

  for (const taskKey of taskKeys) {
    describe(taskKey, () => {
      const route = new ActivatedRouteStub({ index: 0 }, null, {
        taskKey: taskKey,
      });

      beforeEach(async () => {
        await TestBed.configureTestingModule({
          imports: [SharedModule, N2oModule, MeasurementModule, RouterTestingModule],
          providers: [
            { provide: ActivatedRoute, useValue: route },
            {
              provide: PermitApplicationStore,
              useExisting: PermitIssuanceStore,
            },
          ],
        }).compileComponents();
      });

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
                        noHighestRequiredTierJustification: {
                          isCostUnreasonable: true,
                          isTechnicallyInfeasible: true,
                          technicalInfeasibilityExplanation: 'This is an explanation',
                        },
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
              [`${taskKey}_Measured_Emissions`]: [true],
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

      it('should populate applied standard properties', () => {
        expect(page.measuredEmissionsProperties.map((prop) => prop.textContent.trim())).toEqual(
          expect.arrayContaining([
            'ref1, Ultrasonic meter , Specified uncertainty ±2.0',
            'Monthly',
            'Tier 1',
            'No',
            'Unreasonable cost  Technical infeasibility',
            'This is an explanation',
          ]),
        );
      });
    });
  }
});
