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
import { AnalysisMethodListTemplateComponent } from './analysis-method-list-template.component';

describe('AnalysisMethodListTemplateComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: AnalysisMethodListTemplateComponent;
  let fixture: ComponentFixture<AnalysisMethodListTemplateComponent>;
  let page: Page;

  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
    statusKey: 'MEASUREMENT_CO2_Biomass_Fraction',
  });

  const emissionPointCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
  ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

  class Page extends BasePage<AnalysisMethodListTemplateComponent> {
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
                    oneThirdRule: false,
                    tier: 'TIER_3',
                    defaultValueApplied: undefined,
                    analysisMethodUsed: true,
                    analysisMethods: [
                      {
                        analysis: 'analyze',
                        files: [],
                        frequencyMeetsMinRequirements: true,
                        laboratoryAccredited: false,
                        laboratoryName: 'lab',
                        samplingFrequency: 'WEEKLY',
                        subParameter: null,
                      },
                    ],
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
    fixture = TestBed.createComponent(AnalysisMethodListTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display analysis method list', () => {
    expect(page.answers).toEqual([
      ['Analysis method added', ''],
      ['Method of analysis', 'analyze'],
      ['Sub parameter', ''],
      ['Sampling frequency', 'Weekly'],
      ['Sampling frequency requirements met?', 'Yes'],
      ['Laboratory name', 'lab'],
      ['Is the laboratory ISO17025 accredited?', 'No'],
    ]);
  });
});
