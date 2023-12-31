import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationOfPFCMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../../../shared/shared.module';
import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockStateBuild } from '../../../../../testing/mock-state';
import { PFCModule } from '../../../pfc.module';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, { statusKey: 'CALCULATION_PFC_Emission_Factor' });

  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  const technicalInfeasibilityExplanation = 'technicalInfeasibilityExplanation';

  class Page extends BasePage<AnswersComponent> {
    get heading() {
      return this.query<HTMLElement>('.govuk-heading-l');
    }
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PFCModule, RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('for emission factor with highest required tier', () => {
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
                    sourceStreamCategory,
                    emissionFactor: {
                      tier: 'TIER_1',
                      isHighestRequiredTier: true,
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_PFC_Category: [true],
            CALCULATION_PFC_Emission_Factor: [false],
          },
        ),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display answers and submit task status', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.heading.textContent.trim()).toEqual('Check your answers');
      expect(page.answers).toEqual([
        ['Tier applied', 'Tier 1'],
        ['Highest required tier?', 'Yes'],
      ]);

      page.confirmButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              CALCULATION_PFC: {
                ...store.getState().permit.monitoringApproaches.CALCULATION_PFC,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory,
                    emissionFactor: {
                      tier: 'TIER_1',
                      isHighestRequiredTier: true,
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            CALCULATION_PFC_Emission_Factor: [true],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route, state: { notification: true } });
    });
  });

  describe('for emission factor with no highest required tier', () => {
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
                    emissionFactor: {
                      tier: 'TIER_1',
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isCostUnreasonable: false,
                        isTechnicallyInfeasible: true,
                        technicalInfeasibilityExplanation,
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_PFC_Category: [true],
            CALCULATION_PFC_Emission_Factor: [false],
          },
        ),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display answers and submit task status', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.heading.textContent.trim()).toEqual('Check your answers');
      expect(page.answers).toEqual([
        ['Tier applied', 'Tier 1'],
        ['Highest required tier?', 'No'],
        ['Reasons for not applying the highest required tier', 'Technical infeasibility'],
        ['Explanation of technical infeasibility', technicalInfeasibilityExplanation],
      ]);

      page.confirmButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              CALCULATION_PFC: {
                ...store.getState().permit.monitoringApproaches.CALCULATION_PFC,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                    emissionFactor: {
                      tier: 'TIER_1',
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isCostUnreasonable: false,
                        isTechnicallyInfeasible: true,
                        technicalInfeasibilityExplanation,
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            CALCULATION_PFC_Emission_Factor: [true],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route, state: { notification: true } });
    });
  });
});
