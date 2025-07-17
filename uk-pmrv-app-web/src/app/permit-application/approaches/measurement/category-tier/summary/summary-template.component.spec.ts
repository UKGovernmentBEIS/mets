import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { MeasurementOfCO2MonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { MeasurementModule } from '../../measurement.module';
import { SummaryTemplateComponent } from './summary-template.component';

describe('SummaryTemplateComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: SummaryTemplateComponent;
  let fixture: ComponentFixture<SummaryTemplateComponent>;
  let page: Page;

  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
    statusKey: 'MEASUREMENT_CO2_Biomass_Fraction',
  });

  const emissionPointCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
  ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

  class Page extends BasePage<SummaryTemplateComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeasurementModule, RouterTestingModule, SharedModule],
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
            MEASUREMENT_CO2: {
              type: 'MEASUREMENT_CO2',
              emissionPointCategoryAppliedTiers: [
                {
                  emissionPointCategory,
                  biomassFraction: {
                    exist: true,
                    tier: 'TIER_2B',
                    isHighestRequiredTier: true,
                    defaultValueApplied: true,
                    standardReferenceSource: {
                      type: 'MONITORING_REPORTING_REGULATION_ARTICLE_36_3',
                    },
                    analysisMethodUsed: false,
                  },
                },
              ],
            },
          },
        },
        {
          MEASUREMENT_CO2_Biomass_Fraction: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display analysis method list', () => {
    expect(page.answers).toEqual([
      ['Details', ''],
      ['Using biomass fraction as part of your calculation?', 'Yes'],
      ['Tier applied', 'Tier 2b'],
      ['Applying the highest required tier?', 'Yes'],
      ['Applying a default value', 'Yes'],
      ['Standard reference source', ''],
      ['Using an analysis method?', 'No'],
    ]);
  });
});
