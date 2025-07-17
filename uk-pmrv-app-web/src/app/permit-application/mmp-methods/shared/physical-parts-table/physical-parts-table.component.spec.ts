import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { mockDigitizedPlanDetails } from '@permit-application/mmp-sub-installations/testing/mock';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { DigitizedPlan, SubInstallation } from 'pmrv-api';

import { PhysicalPartsTableComponent } from './physical-parts-table.component';

describe('PhysicalPartsTableComponent', () => {
  let component: PhysicalPartsTableComponent;
  let fixture: ComponentFixture<PhysicalPartsTableComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });
  const createComponent = (createError?: boolean) => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              methodTask: mockDigitizedPlanDetails.methodTask,
              subInstallations: [
                ...mockDigitizedPlanDetails.subInstallations,
                {
                  subInstallationNo: '1',
                  subInstallationType: !createError ? 'ADIPIC_ACID' : 'AROMATICS',
                } as SubInstallation,
              ],
            } as DigitizedPlan,
          },
        },
        {
          MMP_SUB_INSTALLATION_Product_Benchmark: [true, true],
        },
      ),
    );

    fixture = TestBed.createComponent(PhysicalPartsTableComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  class Page extends BasePage<PhysicalPartsTableComponent> {
    get tableValues() {
      return this.queryAll('td').map((cell) => cell.textContent.trim());
    }

    get links() {
      return this.queryAll<HTMLElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhysicalPartsTableComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: PermitApplicationStore, useExisting: PermitIssuanceStore },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('should show table content', () => {
    createComponent();

    expect(page.tableValues).toEqual(['Test 1', 'Adipic acid  Ammonia', '', 'Remove', 'Change']);
  });

  it('should show table content with error', () => {
    createComponent(true);

    expect(page.tableValues).toEqual(['Test 1', 'Select at least two sub-installations', '', 'Remove', 'Change']);
  });
});
