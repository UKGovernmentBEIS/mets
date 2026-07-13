import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { N2oComponent } from './n2o.component';
import { N2oModule } from './n2o.module';

describe('N2oComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: N2oComponent;
  let fixture: ComponentFixture<N2oComponent>;

  class Page extends BasePage<N2oComponent> {
    get tasks() {
      return this.queryAll<HTMLLIElement>('li');
    }
    get tables() {
      return this.queryAll<HTMLTableElement>('govuk-table');
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('govuk-table tr')
        .filter((el) => !el.querySelector('th'))
        .map((el) => Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(N2oComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, N2oModule],
      providers: [
        provideRouter([]),
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('without emission point categories', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            MEASUREMENT_N2O: {},
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks', () => {
      expect(page.tables.length).toEqual(1);
      expect(page.rows).toEqual([['Measurement of nitrous oxide (N2O)', '0t', '0t', '0t', '0t']]);

      expect(page.tasks).toBeTruthy();
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('.app-task-list__tag').textContent?.trim(),
        ]),
      ).toEqual([
        ['Add an emission point category', 'Cannot start yet'],
        ['Approach description', 'Not started'],
        ['Emission determination procedure', 'Not started'],
        ['Determination of reference period', 'Not started'],
        ['Operational management', 'Not started'],
        ['Determination of nitrous oxide emissions', 'Not started'],
        ['Determination of nitrous oxide concentration', 'Not started'],
        ['Determination of the quantity of product produced', 'Not started'],
        ['Quantity of materials', 'Not started'],
        ['Calculation of gas flow', 'Not started'],
      ]);
    });
  });

  describe('with emission point categories', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockStateBuild({
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
                },
                {
                  emissionPointCategory: {
                    sourceStreams: ['16236817394240.1574963093314663'],
                    emissionSources: ['16245246343280.27155194483385103'],
                    emissionPoint: 'unknown',
                    emissionType: 'ABATED',
                    monitoringApproachType: 'CALCULATION',
                    annualEmittedCO2Tonnes: '23.5',
                    categoryType: 'MAJOR',
                  },
                },
              ],
            },
          },
        }),
        permitSectionsCompleted: {
          MEASUREMENT_N2O_Category: [true, true],
          MEASUREMENT_N2O_Measured_Emissions: [true, true],
          MEASUREMENT_N2O_Applied_Standard: [true, true],
          emissionPoints: [true],
          emissionSources: [true],
          sourceStreams: [true],
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks', () => {
      expect(page.tables.length).toEqual(2);
      expect(page.rows).toEqual([
        ['Measurement of nitrous oxide (N2O)', '0t', '0t', '0t', '47t'],
        ['The big Ref Emission point 1: Major', '23.5 t (50%)', 'Completed'],
        ['UNDEFINED: Major', '23.5 t (50%)', 'Needs review'],
      ]);

      expect(page.tasks).toBeTruthy();
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('.app-task-list__tag').textContent?.trim(),
        ]),
      ).toEqual([
        ['Approach description', 'Not started'],
        ['Emission determination procedure', 'Not started'],
        ['Determination of reference period', 'Not started'],
        ['Operational management', 'Not started'],
        ['Determination of nitrous oxide emissions', 'Not started'],
        ['Determination of nitrous oxide concentration', 'Not started'],
        ['Determination of the quantity of product produced', 'Not started'],
        ['Quantity of materials', 'Not started'],
        ['Calculation of gas flow', 'Not started'],
      ]);
    });
  });
});
