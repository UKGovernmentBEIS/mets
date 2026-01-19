import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import {
  mockDigitizedPlanSPAromatics,
  mockDigitizedPlanSPDolime,
  mockDigitizedPlanSPEthyleneOxideGlycols,
  mockDigitizedPlanSPHydrogen,
  mockDigitizedPlanSPLime,
  mockDigitizedPlanSPrefineryProducts,
  mockDigitizedPlanSPSteamCracking,
  mockDigitizedPlanSPSynthesisGas,
  mockDigitizedPlanSPVinylChlorideMonomer,
  mockDigitizedPlanWasteGasBalance,
} from '@permit-application/mmp-sub-installations/testing/mock';
import { PermitApplicationModule } from '@permit-application/permit-application.module';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { mockState } from '../../../testing/mock-state';
import { SubInstallationsSummaryTemplateComponent } from './sub-installations-summary-template.component';

const expected = [
  [
    'Ammonia',
    'Change product benchmark',
    'description',
    'Change  description of the system boundaries of this sub-installation',
    '',
    'Change  sub-installation details supporting files',
  ],
  [
    'Quantities of products:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
    'Change  annual production levels data Source',
    '5. (a) based on continual metering at the process where the material is consumed or produced',
    'Change  method used to determine the annual quantity of products',
    'description',
    'Change  annual production levels description of methodology applied for each data source',
    'Yes',
    'Change  if hierarchical order has been followed, annual production levels',
    'description',
    'Change  description of the methodology used for keeping track of the products produced',
    '',
    'Change  annual production levels supporting files',
  ],
  [
    'Relevant electricity consumption:  4.5.(a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU',
    'Change  exchangeability of fuel and electricity data source',
    'description',
    'Change  exchangeability of fuel and electricity description of methodology applied for each data source',
    'Yes',
    'Change  if hierarchical order has been followed, exchangeability of fuel and electricity',
    '',
    'Change  exchangeability of fuel and electricity supporting files',
  ],
  ['No', 'Change  if there are any measurable heat flows imported from non-ETS installations or entities'],
  [
    'attribution',
    'Change  attribution of directly attributable emissions',
    'Yes',
    'Change  if the further internal source streams are relevant',
    'Amounts imported or exported:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
    'Change  directly attributable emissions data source',
    'description',
    'Change  directly attributable emissions description of methodology applied for each data source',
    'Yes',
    'Change  if any transferred CO2 is imported or exported',
    'description',
    'Change  description of how the amounts are monitored, if not already covered by the monitoring plan under the MRR',
    '',
    'Change  directly attributable emissions supporting files',
  ],
  [
    'Yes',
    'Change  fuel input and relevant emission factor',
    'Fuel input:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Weighted emission factor:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
    'Change  fuel input and relevant emission factor data source',
    'description',
    'Change  fuel input and relevant emission factor description of methodology applied for each data source',
    'Yes',
    'Change  if the hierarchical order has been followed, fuel input and relevant emission factor',
    '',
    'Change  fuel input and relevant emission factor supporting files',
  ],
  [
    'Measurable heat imported',
    'Change  if any measurable heat is imported to or exported from this sub-installation',
    'Net measurable heat flows:  7.2. Method 1: Using measurements',
    'Change  imported and exported measurable heat data source',
    'description',
    'Change  imported and exported measurable heat description of methodology applied for each data source',
    'Yes',
    'Change  if the hierarchical order has been followed, imported and exported measurable heat',
    'description',
    'Change  description of the methodology for determination of the relevant attributable emission factors',
    'No',
    'Change  if there are any measurable heat flows imported from sub-installations producing pulp',
    '',
    'Change  imported and exported measurable heat supporting files',
  ],
  [
    'Waste gas produced',
    'Change  waste gas balance',
    'Waste gases produced:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
    'Change  waste gas balance data source',
    'description',
    'Change  waste gas balance description of methodology applied for each data source',
    'Yes',
    'Change  if the hierarchical order has been followed, waste gas balance',
    '',
    'Change  waste gas balance supporting files',
  ],
];

describe('SubInstallationsSummaryTemplateComponent', () => {
  let component: SubInstallationsSummaryTemplateComponent;
  let fixture: ComponentFixture<SubInstallationsSummaryTemplateComponent>;
  let page: Page;
  let store: PermitApplicationStore<any>;
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<SubInstallationsSummaryTemplateComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((data) =>
        Array.from(data.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubInstallationsSummaryTemplateComponent],
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
            digitizedPlan: mockDigitizedPlanWasteGasBalance,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsSummaryTemplateComponent);
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

  it('should show summary for refinery', () => {
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanSPrefineryProducts,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );

    fixture.detectChanges();

    expect(page.summaryListValues?.[8]).toEqual([
      'Atmospheric Crude Distillation:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
      'Change  calculation of historical activity levels data source',
      'description',
      'Change  calculation of historical activity levels description of methodology applied for each data source',
      'Yes',
      'Change  if the hierarchical order has been followed, calculation of historical activity levels',
      '',
      'Change  calculation of historical activity levels supporting files',
    ]);
  });

  it('should show summary with lime SP', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanSPLime,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.summaryListValues).toEqual([
      ...expected,
      [
        'Composition data:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
        'Change  calculation of historical activity levels data source',
        'description',
        'Change  calculation of historical activity levels description of methodology applied for each data source',
        'Yes',
        'Change  if the hierarchical order has been followed, calculation of historical activity levels',
        '',
        'Change  calculation of historical activity levels supporting files',
      ],
    ]);
  });

  it('should show summary with dolime SP', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanSPDolime,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.summaryListValues).toEqual([
      ...expected,
      [
        'Composition data:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
        'Change  calculation of historical activity levels data source',
        'description',
        'Change  calculation of historical activity levels description of methodology applied for each data source',
        'Yes',
        'Change  if the hierarchical order has been followed, calculation of historical activity levels',
        '',
        'Change  calculation of historical activity levels supporting files',
      ],
    ]);
  });

  it('should show summary with steam cracking SP', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanSPSteamCracking,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.summaryListValues).toEqual([
      ...expected,
      [
        'Hydrogen, ethylene and other HVC:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
        'Change  calculation of historical activity levels data source',
        'description',
        'Change  calculation of historical activity levels description of methodology applied for each data source',
        'Yes',
        'Change  if the hierarchical order has been followed, calculation of historical activity levels',
        '',
        'Change  calculation of historical activity levels supporting files',
      ],
    ]);
  });

  it('should show summary with aromatics SP', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanSPAromatics,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.summaryListValues).toEqual([
      ...expected,
      [
        'Naphtha/Gasoline Hydrotreater:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
        'Change  calculation of historical activity levels data source',
        'description',
        'Change  calculation of historical activity levels description of methodology applied for each data source',
        'Yes',
        'Change  if the hierarchical order has been followed, calculation of historical activity levels',
        '',
        'Change  calculation of historical activity levels supporting files',
      ],
    ]);
  });

  it('should show summary with hydrogen SP', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanSPHydrogen,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.summaryListValues).toEqual([
      ...expected,
      [
        'Total hydrogen production:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Volume fraction of hydrogen:  4.6.(b) Laboratory analyses in accordance with section 6.1 of  Annex VII (FAR)',
        'Change  calculation of historical activity levels data source',
        'description',
        'Change  calculation of historical activity levels description of methodology applied for each data source',
        'Yes',
        'Change  if the hierarchical order has been followed, calculation of historical activity levels',
        '',
        'Change  calculation of historical activity levels supporting files',
      ],
    ]);
  });

  it('should show summary with synthesis gas SP', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanSPSynthesisGas,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.summaryListValues).toEqual([
      ...expected,
      [
        'Total synthesis gas production:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Composition data:  4.6.(b) Laboratory analyses in accordance with section 6.1 of  Annex VII (FAR)',
        'Change  calculation of historical activity levels data source',
        'description',
        'Change  calculation of historical activity levels description of methodology applied for each data source',
        'Yes',
        'Change  if the hierarchical order has been followed, calculation of historical activity levels',
        '',
        'Change  calculation of historical activity levels supporting files',
      ],
    ]);
  });

  it('should show summary with etheline oxide/etheline glycols SP', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanSPEthyleneOxideGlycols,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.summaryListValues).toEqual([
      ...expected,
      [
        'Ethylene oxide:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Monoethylene glycol:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Diethylene glycol:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Triethylene glycol:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
        'Change  calculation of historical activity levels data source',
        'description',
        'Change  calculation of historical activity levels description of methodology applied for each data source',
        'Yes',
        'Change  if the hierarchical order has been followed, calculation of historical activity levels',
        '',
        'Change  calculation of historical activity levels supporting files',
      ],
    ]);
  });

  it('should show summary with vinyl chloride monomer SP', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanSPVinylChlorideMonomer,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SubInstallationsSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();

    expect(page.summaryListValues).toEqual([
      ...expected,
      [
        "Quantification of heat from hydrogen:  4.5.(c) Readings of measuring instruments not under the operator's control for direct determination of a data set not falling under point a",
        'Change  calculation of historical activity levels data source',
        'description',
        'Change  calculation of historical activity levels description of methodology applied for each data source',
        'Yes',
        'Change  if the hierarchical order has been followed, calculation of historical activity levels',
        '',
        'Change  calculation of historical activity levels supporting files',
      ],
    ]);
  });
});
