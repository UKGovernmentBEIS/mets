import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { MeasurementOfCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { MeasurementModule } from '../../measurement.module';
import { TierComponent } from './tier.component';

describe('TierComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: TierComponent;
  let fixture: ComponentFixture<TierComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
    statusKey: 'MEASUREMENT_CO2_Biomass_Fraction',
  });
  const emissionPointCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
  ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

  class Page extends BasePage<TierComponent> {
    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get tierRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="tier"]');
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get isHighestTierRequiredT1Options() {
      return this.queryAll<HTMLInputElement>('input[name$="isHighestRequiredTier_TIER_1"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TierComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeasurementModule, RouterTestingModule, SharedModule],
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

  describe('for new biomass fraction', () => {
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
                    },
                  },
                ],
              },
            },
          },
          {
            MEASUREMENT_CO2_Category: [true],
            MEASUREMENT_CO2_Biomass_Fraction: [false],
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

    it('should submit a valid form', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a tier']);

      page.tierRadios[1].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select yes or no']);

      page.tierRadios[2].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select yes or no']);

      page.tierRadios[3].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select yes or no']);

      page.tierRadios[0].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              MEASUREMENT_CO2: {
                type: 'MEASUREMENT_CO2',
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory: (
                      mockState.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
                    ).emissionPointCategoryAppliedTiers[0].emissionPointCategory,
                    biomassFraction: {
                      exist: true,
                      tier: 'TIER_3',
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            MEASUREMENT_CO2_Category: [true],
            MEASUREMENT_CO2_Biomass_Fraction: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../default-value'], { relativeTo: route });
    });
  });

  describe('for existing biomass fraction', () => {
    beforeEach(() => {
      const emissionPointCategory = (
        mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
      ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

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
                      tier: 'TIER_1',
                      isHighestRequiredTier: true,
                      exist: true,
                    },
                  },
                ],
              },
            },
          },
          {
            MEASUREMENT_CO2_Category: [true],
            MEASUREMENT_CO2_Biomass_Fraction: [false],
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

    it('should fill the form', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.tierRadios.length).toEqual(4);
      expect(page.tierRadios[0].checked).toBeFalsy();
      expect(page.tierRadios[1].checked).toBeFalsy();
      expect(page.isHighestTierRequiredT1Options[0].checked).toBeTruthy();
      expect(page.isHighestTierRequiredT1Options[1].checked).toBeFalsy();
    });
  });
});
