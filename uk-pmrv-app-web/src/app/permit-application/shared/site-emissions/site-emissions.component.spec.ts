import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { MonitoringApproachDescriptionPipe } from '../../../shared/pipes/monitoring-approach-description.pipe';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { SharedPermitModule } from '../shared-permit.module';
import { SiteEmissionsComponent } from './site-emissions.component';

describe('SiteEmissionsComponent', () => {
  let page: Page;
  let component: SiteEmissionsComponent;
  let fixture: ComponentFixture<SiteEmissionsComponent>;
  let store: PermitApplicationStore<PermitApplicationState>;

  class Page extends BasePage<SiteEmissionsComponent> {
    set selectValue(value: string) {
      this.setInputValue('select', value);
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('govuk-table tr')
        .filter((el) => !el.querySelector('th'))
        .map((el) => Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SiteEmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedPermitModule],
      providers: [
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
        MonitoringApproachDescriptionPipe,
      ],
    }).compileComponents();
  });

  describe('with all monitoring approaches', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('show site emissions table', () => {
      expect(page.rows).toEqual([
        ['Calculation of CO2', '0t', '0t', '0t', '0t'],
        ['Calculation of perfluorocarbons (PFC)', '0t', '0t', '0t', '23.5t'],
        ['Measurement of nitrous oxide (N2O)', '0t', '0t', '0t', '23.5t'],
        ['Fallback approach', '0t', '0t', '0t', '23.8t'],
        ['Measurement of CO2', '0t', '0t', '0t', '23.8t'],
        ['Calculate Transferred CO2', '0t', '0t', '0t', '-23.5t'],
        ['Measure Transferred N2O', '0t', '0t', '0t', '0t'],
        ['Measure Transferred CO2', '0t', '0t', '0t', '0t'],
        ['Total', '0t', '0t', '0t', '71.1t'],
      ]);

      page.selectValue = 'false';
      fixture.detectChanges();

      expect(page.rows).toEqual([
        ['Calculation of CO2', '0%', '0%', '0%', '0%'],
        ['Calculation of perfluorocarbons (PFC)', '0%', '0%', '0%', '33%'],
        ['Measurement of nitrous oxide (N2O)', '0%', '0%', '0%', '33%'],
        ['Fallback approach', '0%', '0%', '0%', '33%'],
        ['Measurement of CO2', '0%', '0%', '0%', '33%'],
        ['Calculate Transferred CO2', '0%', '0%', '0%', ''],
        ['Measure Transferred N2O', '0%', '0%', '0%', '0%'],
        ['Measure Transferred CO2', '0%', '0%', '0%', '0%'],
        ['Total', '0%', '0%', '0%', '100%'],
      ]);
    });
  });

  describe('with missing monitoring approaches tiers', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            ...mockPermitApplyPayload.permit.monitoringApproaches,
            FALLBACK: {},
          },
        }),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('show site emissions table', () => {
      expect(page.rows).toEqual([
        ['Calculation of CO2', '0t', '0t', '0t', '0t'],
        ['Calculation of perfluorocarbons (PFC)', '0t', '0t', '0t', '23.5t'],
        ['Measurement of nitrous oxide (N2O)', '0t', '0t', '0t', '23.5t'],
        ['Fallback approach', '0t', '0t', '0t', '0t'],
        ['Measurement of CO2', '0t', '0t', '0t', '23.8t'],
        ['Calculate Transferred CO2', '0t', '0t', '0t', '-23.5t'],
        ['Measure Transferred N2O', '0t', '0t', '0t', '0t'],
        ['Measure Transferred CO2', '0t', '0t', '0t', '0t'],
        ['Total', '0t', '0t', '0t', '47.3t'],
      ]);

      page.selectValue = 'false';
      fixture.detectChanges();

      expect(page.rows).toEqual([
        ['Calculation of CO2', '0%', '0%', '0%', '0%'],
        ['Calculation of perfluorocarbons (PFC)', '0%', '0%', '0%', '50%'],
        ['Measurement of nitrous oxide (N2O)', '0%', '0%', '0%', '50%'],
        ['Fallback approach', '0%', '0%', '0%', '0%'],
        ['Measurement of CO2', '0%', '0%', '0%', '50%'],
        ['Calculate Transferred CO2', '0%', '0%', '0%', ''],
        ['Measure Transferred N2O', '0%', '0%', '0%', '0%'],
        ['Measure Transferred CO2', '0%', '0%', '0%', '0%'],
        ['Total', '0%', '0%', '0%', '100%'],
      ]);
    });
  });

  describe('with one monitoring approach defined with no values', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            CALCULATION_PFC: {},
          },
        }),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('show site emissions table', () => {
      expect(page.rows).toEqual([['Calculation of perfluorocarbons (PFC)', '0t', '0t', '0t', '0t']]);

      page.selectValue = 'false';
      fixture.detectChanges();

      expect(page.rows).toEqual([['Calculation of perfluorocarbons (PFC)', '0%', '0%', '0%', '0%']]);
    });
  });
});
