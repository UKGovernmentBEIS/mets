import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockState, mockStateBuild } from '../../../testing/mock-state';
import { MeasurementModule } from '../measurement.module';
import { CategoryTierComponent } from './category-tier.component';

describe('CategoryTierComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: CategoryTierComponent;
  let fixture: ComponentFixture<CategoryTierComponent>;

  class Page extends BasePage<CategoryTierComponent> {
    get heading() {
      return this.query<HTMLElement>('h1');
    }
    get tasks() {
      return this.queryAll<HTMLLIElement>('li');
    }
    get summaryDefinitions() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(CategoryTierComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, MeasurementModule, RouterTestingModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('for emission point categories that cannot start', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            MEASUREMENT_CO2: {},
          },
          permitSectionsCompleted: {
            sourceStreams: [false],
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the not started page', () => {
      expect(page.heading.textContent.trim()).toEqual('Add an emission point category');
      expect(page.tasks.map((el) => el.textContent.trim())).toEqual([
        'Source streams (fuels and materials)',
        'Emission sources',
        'Emission points',
      ]);
    });
  });

  describe('for emission point categories that can start', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockStateBuild(mockState, {
          ...mockState.permitSectionsCompleted,
          MEASUREMENT_CO2_Category: [true],
          MEASUREMENT_CO2_Measured_Emissions: [false],
          MEASUREMENT_CO2_Applied_Standard: [false],
          MEASUREMENT_CO2_Biomass_Fraction: [false],
          emissionPoints: [true],
          emissionSources: [true],
          sourceStreams: [true],
        }),
        features: {
          wastePermitEnabled: true,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('The big Ref Emission point 1: Major  Delete');
      expect(page.tasks.map((el) => el.querySelector('a').textContent.trim())).toEqual([
        'Emission point category',
        'Measured emissions',
        'Applied standard',
        'Biomass fraction',
      ]);

      expect(page.tasks.map((el) => el.querySelector('govuk-tag').textContent.trim())).toEqual([
        'completed',
        'cannot start yet',
        'not started',
        'not started',
      ]);

      expect(page.summaryDefinitions).toEqual([
        ['Source stream category', '13123124 White Spirit & SBP'],
        ['Emission sources', 'S1 Boiler'],
        ['Emission point', 'The big Ref Emission point 1'],
        ['Estimated CO2 emitted', '23.8 tonnes'],
        ['Are the emissions from this source stream exported to, or received from another installation?', 'No'],
      ]);
    });
  });

  describe('for emission point categories that can start but emission sources are in pending status', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockStateBuild(mockState, {
          ...mockState.permitSectionsCompleted,
          MEASUREMENT_CO2_Category: [true],
          MEASUREMENT_CO2_Measured_Emissions: [false],
          MEASUREMENT_CO2_Applied_Standard: [true],
          MEASUREMENT_CO2_Biomass_Fraction: [false],
          emissionPoints: [true],
          emissionSources: [false],
          sourceStreams: [true],
        }),
        features: {
          wastePermitEnabled: true,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('The big Ref Emission point 1: Major  Delete');
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([
        ['Emission point category', 'completed'],
        ['Measured emissions', 'cannot start yet'],
        ['Applied standard', 'completed'],
        ['Biomass fraction', 'not started'],
      ]);
    });
  });

  describe('for emission point categories that needs review', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT_CO2: {
                type: 'MEASUREMENT_CO2',
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory: {
                      sourceStreams: ['16236817394240.1574963093314663'],
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoint: 'unknown',
                      emissionType: 'ABATED',
                      monitoringApproachType: 'CALCULATION',
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            MEASUREMENT_CO2_Category: [true],
            MEASUREMENT_CO2_Measured_Emissions: [false],
            MEASUREMENT_CO2_Applied_Standard: [false],
            MEASUREMENT_CO2_Biomass_Fraction: [false],
          },
        ),
        features: {
          wastePermitEnabled: true,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('UNDEFINED: Major  Delete');
      expect(page.tasks.map((el) => el.textContent.trim())).toEqual([
        'Emission point category needs review',
        'Measured emissions cannot start yet',
        'Applied standard cannot start yet',
        'Biomass fraction not started',
      ]);
    });
  });

  describe('for emission point categories with deleted emission points', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockStateBuild(
          {
            emissionSources: [],
          },
          {
            ...mockState.permitSectionsCompleted,
            MEASUREMENT_CO2_Category: [true],
            MEASUREMENT_CO2_Measured_Emissions: [false],
            MEASUREMENT_CO2_Applied_Standard: [false],
            MEASUREMENT_CO2_Biomass_Fraction: [false],
          },
        ),
        features: {
          wastePermitEnabled: true,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('The big Ref Emission point 1: Major  Delete');
      expect(page.tasks.map((el) => el.textContent.trim())).toEqual([
        'Emission point category needs review',
        'Measured emissions cannot start yet',
        'Applied standard cannot start yet',
        'Biomass fraction not started',
      ]);
    });
  });
});
