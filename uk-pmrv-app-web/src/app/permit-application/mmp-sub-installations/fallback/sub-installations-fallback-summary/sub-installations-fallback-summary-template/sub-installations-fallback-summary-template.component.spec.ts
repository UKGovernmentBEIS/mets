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
  [
    'Process emissions exposed to carbon leakage',
    'Change  sub-installation type',
    'description',
    'Change  description the system boundaries of this sub-installation',
    '',
    'Change  sub-installation details supporting files',
  ],
  [
    'description',
    'Change  annual activity levels description of methodology applied for each data source',
    'description',
    'Change  description of the methodology used for keeping track of the products produced',
    '',
    'Change  annual activity levels supporting files',
  ],
];

const expectedHeat = [
  [
    'Heat benchmark exposed to carbon leakage',
    'Change  sub-installation type',
    'description',
    'Change  description the system boundaries of this sub-installation',
    '',
    'Change  sub-installation details supporting files',
  ],
  [
    'Quantification of measurable heat flows:  4.5.(a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU  Net measurable heat flows:  7.2. Method 1: Using measurements',
    'Change  annual activity levels data source',
    'description',
    'Change  annual activity levels description of methodology applied for each data source',
    'Yes',
    'Change  if the hierarchical order has been followed, annual activity levels',
    'description',
    'Change  description of the methodology used for keeping track of the products produced',
    '',
    'Change  annual activity levels supporting files',
  ],
  [
    'attribution',
    'Change  attribution of directly attributable emissions',
    '',
    'Change  directly attributable emissions supporting files',
  ],
  [
    'Yes',
    'Change  fuel input and relevant emission factor',
    'Yes',
    'Change  if there is any fuel input from waste gases',
    'Fuel input:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Net calorific value:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Weighted emission factor:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Fuel input from waste gases:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Net calorific value for waste gas:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Emission factor:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
    'Change  fuel input and relevant emission factor data source',
    'description',
    'Change  fuel input and relevant emission factor description of methodology applied for each data source',
    'Yes',
    'Change  if the hierarchical order has been followed, fuel input and relevant emission factor',
    '',
    'Change  fuel input and relevant emission factor supporting files',
  ],
  [
    'Yes',
    'Change  if measurable heat is produced at this sub-installation',
    'Heat produced:  4.5.(a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU',
    'Change  measurable heat produced data source',
    'description',
    'Change  measurable heat produced description of methodology applied for each data source',
    'Yes',
    'Change  if the hierarchical order has been followed, measurable heat produced',
    '',
    'Change  measurable heat produced supporting files',
  ],
  [
    'Imported from other sources',
    'Change  if any measurable heat is imported to this sub-installation',
    'Imported from other sources:  4.5.(a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU  Net measurable heat flows imported from other sources:  7.2. Method 1: Using measurements',
    'Change  measurable heat imported data source',
    'description',
    'Change  measurable heat imported description of methodology applied for each data source',
    'Yes',
    'Change  if the hierarchical order has been followed, measurable heat imported',
    'description',
    'Change  description of the methodology for determination of the relevant attributable emission factors',
    '',
    'Change  measurable heat imported supporting files',
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
