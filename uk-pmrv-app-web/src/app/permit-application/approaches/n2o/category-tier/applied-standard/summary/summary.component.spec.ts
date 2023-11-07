import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { MeasurementOfN2OEmissionPointCategoryAppliedTier } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../../../shared/shared.module';
import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { N2oModule } from '../../../n2o.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let fixture: ComponentFixture<SummaryComponent>;
  let component: SummaryComponent;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  const route = new ActivatedRouteStub({ index: 0 }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT_N2O.emissionPointCategoryAppliedTiers',
    statusKey: 'MEASUREMENT_N2O_Applied_Standard',
  });

  class Page extends BasePage<SummaryComponent> {
    get appliedStandardProperties() {
      return this.queryAll<HTMLDListElement>('dl > div > dd');
    }
    get caption() {
      return this.query<HTMLElement>('.govuk-caption-l');
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, N2oModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('for emission point category in complete status', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT_N2O: {
                type: 'MEASUREMENT_N2O',
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
                    appliedStandard: {
                      parameter: 'parameter',
                      appliedStandard: 'appliedStandard',
                      deviationFromAppliedStandardExist: true,
                      deviationFromAppliedStandardDetails: 'deviationFromAppliedStandardDetails',
                      laboratoryName: 'laboratoryName',
                      laboratoryAccredited: true,
                    },
                  },
                ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
              },
            },
          },
          {
            sourceStreams: [true],
            emissionSources: [true],
            emissionPoints: [true],
            MEASUREMENT_N2O_Category: [true],
            MEASUREMENT_N2O_Applied_Standard: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should populate applied standard properties', () => {
      expect(page.caption.textContent.trim()).toEqual(
        'Measurement of nitrous oxide (N2O), The big Ref Emission point 1: Major',
      );
      expect(page.appliedStandardProperties.map((prop) => prop.textContent.trim())).toEqual(
        expect.arrayContaining([
          'parameter',
          'appliedStandard',
          'Yes  deviationFromAppliedStandardDetails',
          'laboratoryName',
          'Yes',
        ]),
      );
    });
  });

  describe('for emission point category in needs review status', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT_N2O: {
                type: 'MEASUREMENT_N2O',
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory: {
                      sourceStreams: ['16236817394240.1574963093314663'],
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoint: 'unspecified',
                      emissionType: 'ABATED',
                      monitoringApproachType: 'CALCULATION',
                      annualEmittedCO2Tonnes: '23.5',
                      categoryType: 'MAJOR',
                    },
                    appliedStandard: {
                      parameter: 'parameter',
                      appliedStandard: 'appliedStandard',
                      deviationFromAppliedStandardExist: true,
                      deviationFromAppliedStandardDetails: 'deviationFromAppliedStandardDetails',
                      laboratoryName: 'laboratoryName',
                      laboratoryAccredited: true,
                    },
                  },
                ] as MeasurementOfN2OEmissionPointCategoryAppliedTier[],
              },
            },
          },
          {
            sourceStreams: [true],
            emissionSources: [true],
            emissionPoints: [true],
            MEASUREMENT_N2O_Category: [true],
            MEASUREMENT_N2O_Applied_Standard: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should populate applied standard properties', () => {
      expect(page.caption.textContent.trim()).toEqual('Measurement of nitrous oxide (N2O), UNDEFINED: Major');
      expect(page.appliedStandardProperties.map((prop) => prop.textContent.trim())).toEqual(
        expect.arrayContaining([
          'parameter',
          'appliedStandard',
          'Yes  deviationFromAppliedStandardDetails',
          'laboratoryName',
          'Yes',
        ]),
      );
    });
  });
});
