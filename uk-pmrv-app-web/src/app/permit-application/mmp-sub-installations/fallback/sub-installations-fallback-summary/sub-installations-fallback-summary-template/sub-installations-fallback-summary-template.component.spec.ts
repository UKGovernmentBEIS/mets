import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import {
  mockDigitizedPlanAnnualProcessLevel,
  mockDigitizedPlanMeasurableHeatImported,
} from '@permit-application/mmp-sub-installations/testing/mock';
import { PermitApplicationModule } from '@permit-application/permit-application.module';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { mockState } from '../../../../testing/mock-state';
import { SubInstallationsFallbackSummaryTemplateComponent } from './sub-installations-fallback-summary-template.component';

const expected = [
  ['Process emissions exposed to carbon leakage', 'Change', 'description', 'Change', '', 'Change'],
  ['description', 'Change', 'description', 'Change', '', 'Change'],
];

const expectedHeat = [
  ['Heat benchmark exposed to carbon leakage', 'Change', 'description', 'Change', '', 'Change'],
  [
    'Quantification of measurable heat flows:  4.5.(a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU  Net measurable heat flows:  7.2. Method 1: Using measurements',
    'Change',
    'description',
    'Change',
    'Yes',
    'Change',
    'description',
    'Change',
    '',
    'Change',
  ],
  ['attribution', 'Change', '', 'Change'],
  [
    'Yes',
    'Change',
    'Yes',
    'Change',
    'Fuel input:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Net calorific value:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Weighted emission factor:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Fuel input from waste gases:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Net calorific value for waste gas:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Emission factor:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
    'Change',
    'description',
    'Change',
    'Yes',
    'Change',
    '',
    'Change',
  ],
  [
    'Yes',
    'Change',
    'Heat produced:  4.5.(a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU',
    'Change',
    'description',
    'Change',
    'Yes',
    'Change',
    '',
    'Change',
  ],
  [
    'Imported from other sources',
    'Change',
    'Imported from other sources:  4.5.(a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU  Net measurable heat flows imported from other sources:  7.2. Method 1: Using measurements',
    'Change',
    'description',
    'Change',
    'Yes',
    'Change',
    'description',
    'Change',
    '',
    'Change',
  ],
];

describe('SubInstallationsFallbackSummaryTemplateComponent', () => {
  let component: SubInstallationsFallbackSummaryTemplateComponent;
  let fixture: ComponentFixture<SubInstallationsFallbackSummaryTemplateComponent>;
  let page: Page;
  let store: PermitApplicationStore<any>;
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<SubInstallationsFallbackSummaryTemplateComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((data) =>
        Array.from(data.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubInstallationsFallbackSummaryTemplateComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, PermitApplicationModule],
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
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanAnnualProcessLevel,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Fallback_Approach: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsFallbackSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary', () => {
    expect(page.summaryListValues).toEqual(expected);
  });

  it('should show summary for heat', () => {
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanMeasurableHeatImported,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Fallback_Approach: [false],
        },
      ),
    );

    fixture.detectChanges();

    expect(page.summaryListValues).toEqual(expectedHeat);
  });
});
