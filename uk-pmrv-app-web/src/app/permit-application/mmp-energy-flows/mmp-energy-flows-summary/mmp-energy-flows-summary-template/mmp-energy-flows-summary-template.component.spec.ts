import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { energyFlowsDigitizedPlan } from '@permit-application/mmp-energy-flows/testing/energy-flows-testing.mock';
import { IncludeAnswerDetailsComponent } from '@permit-application/mmp-sub-installations/shared/include-answer-details.component';
import { PermitApplicationModule } from '@permit-application/permit-application.module';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { mockState } from '../../../testing/mock-state';
import { MmpEnergyFlowsSummaryTemplateComponent } from './mmp-energy-flows-summary-template.component';

const expected = [
  [
    'Fuel input:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Energy content:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
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
    'Fuel input:  4.5.(a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU  Energy content:  7.2. Method 1: Using measurements',
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
    'Quantification of waste gas flows:  4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012  Energy content of waste gases:  4.6.(a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012',
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
    'Quantification of energy flows:  4.5.(a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU',
    'Change',
    'description',
    'Change',
    'Yes',
    'Change',
    '',
    'Change',
  ],
];

describe('MmpEnergyFlowsSummaryTemplateComponent', () => {
  let component: MmpEnergyFlowsSummaryTemplateComponent;
  let fixture: ComponentFixture<MmpEnergyFlowsSummaryTemplateComponent>;
  let page: Page;
  let store: PermitApplicationStore<any>;
  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<MmpEnergyFlowsSummaryTemplateComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((data) =>
        Array.from(data.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        SharedModule,
        SharedPermitModule,
        PermitApplicationModule,
        MmpEnergyFlowsSummaryTemplateComponent,
        IncludeAnswerDetailsComponent,
      ],
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
            digitizedPlan: energyFlowsDigitizedPlan,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          mmpEnergyFlows: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(MmpEnergyFlowsSummaryTemplateComponent);
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
});
