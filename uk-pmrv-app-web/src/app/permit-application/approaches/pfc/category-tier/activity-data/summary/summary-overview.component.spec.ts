import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../../../shared/shared.module';
import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { PFCModule } from '../../../pfc.module';
import { SummaryOverviewComponent } from './summary-overview.component';

describe('SummaryOverviewComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: SummaryOverviewComponent;
  let fixture: ComponentFixture<SummaryOverviewComponent>;
  let page: Page;

  const route = new ActivatedRouteStub({ index: '0' }, null, { statusKey: 'CALCULATION_PFC_Activity_Data' });
  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  class Page extends BasePage<SummaryOverviewComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryOverviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PFCModule, RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('for activity data with highest required tier', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              CALCULATION_PFC: {
                type: 'CALCULATION_PFC',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      massBalanceApproachUsed: true,
                      tier: 'TIER_4',
                    },
                  },
                ],
              },
            },
          },
          {},
        ),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display summary list', () => {
      expect(page.answers).toEqual([
        ['Mass balance', 'Yes'],
        ['Tier', 'Tier 4'],
      ]);
    });
  });

  describe('for activity data with no highest required tier', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              CALCULATION_PFC: {
                type: 'CALCULATION_PFC',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      massBalanceApproachUsed: false,
                      tier: 'TIER_1',
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isTechnicallyInfeasible: true,
                        technicalInfeasibilityExplanation: 'technicalInfeasibilityExplanation',
                      },
                    },
                  },
                ],
              },
            },
          },
          {},
        ),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display summary list', () => {
      expect(page.answers).toEqual([
        ['Mass balance', 'No'],
        ['Tier', 'Tier 1'],
        ['Highest required tier', 'No'],
        ['Reasons for not applying the highest required tier', 'Technical infeasibility'],
        ['Explanation of technical infeasibility', 'technicalInfeasibilityExplanation'],
      ]);
    });
  });
});
