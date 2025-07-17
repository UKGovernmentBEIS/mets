import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { createPhysicalPartsListForm } from '@permit-application/mmp-methods/mmp-methods';
import { mockDigitizedPlanDetails } from '@permit-application/mmp-sub-installations/testing/mock';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { DigitizedPlan } from 'pmrv-api';

import { MethodsSummaryTemplateComponent } from './methods-summary-template.component';

describe('MethodsSummaryTemplateComponent', () => {
  let component: MethodsSummaryTemplateComponent;
  let fixture: ComponentFixture<MethodsSummaryTemplateComponent>;
  let store: PermitApplicationStore<PermitApplicationState>;
  let page: Page;

  const state = mockStateBuild(
    {
      monitoringMethodologyPlans: {
        exist: true,
        plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
        digitizedPlan: {
          ...mockDigitizedPlanDetails,
          subInstallations: [
            {
              subInstallationNo: '0',
              subInstallationType: 'AMMONIA',
            },
            {
              subInstallationNo: '1',
              subInstallationType: 'ADIPIC_ACID',
            },
          ],
        } as DigitizedPlan,
      },
    },
    {
      MMP_SUB_INSTALLATION_Fallback_Approach: [true],
      MMP_SUB_INSTALLATION_Product_Benchmark: [true],
    },
  );

  class Page extends BasePage<MethodsSummaryTemplateComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get physicalPartsTable() {
      return this.query<HTMLElement>('app-mmp-physical-parts-table');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MethodsSummaryTemplateComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' }),
        },
        { provide: PermitApplicationStore, useExisting: PermitIssuanceStore },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitIssuanceStore);
    store.setState(state);

    fixture = TestBed.createComponent(MethodsSummaryTemplateComponent);
    component = fixture.componentInstance;
    component.hasTwoOrMoreSubInstallationsCompleted = true;
    component.form = createPhysicalPartsListForm(state);
    component.isEditable = true;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary', () => {
    expect(page.summaryListValues).toEqual([
      ['Are there any physical parts of the installation or units which serve more than one sub-installation?', 'Yes'],
      ['Methods used to assign parts of installations and their emissions to sub-installations', 'Test assign parts'],
      ['Methods used for ensuring that data gaps and double counting are avoided', 'Test avoid double count'],
    ]);
    expect(page.physicalPartsTable).toBeTruthy();
  });
});
