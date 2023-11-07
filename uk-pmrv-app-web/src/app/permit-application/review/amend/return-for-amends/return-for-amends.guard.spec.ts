import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../../permit-variation/store/permit-variation.store';
import { mockPermitVariationReviewOperatorLedPayload } from '../../../../permit-variation/testing/mock';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockReviewRequestActionState, mockReviewState } from '../../../testing/mock-state';
import { ReturnForAmendsGuard } from './return-for-amends.guard';

describe('ReturnForAmendsGuard', () => {
  let guard: ReturnForAmendsGuard;
  let router: Router;

  describe('issuance request', () => {
    let store: PermitIssuanceStore;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule],
        providers: [
          ReturnForAmendsGuard,
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      });
      router = TestBed.inject(Router);
      store = TestBed.inject(PermitIssuanceStore);
      guard = TestBed.inject(ReturnForAmendsGuard);
    });

    it('should activate if action', async () => {
      store.setState({
        ...mockReviewRequestActionState,
      });
      await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(true);

      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          FUELS_AND_EQUIPMENT: {
            type: 'OPERATOR_AMENDS_NEEDED',
            notes: 'Notes',
            changesRequired: 'Changes',
          },
        },
        reviewSectionsCompleted: {
          CONFIDENTIALITY_STATEMENT: true,
          FUELS_AND_EQUIPMENT: true,
          INSTALLATION_DETAILS: true,
          MANAGEMENT_PROCEDURES: true,
          MONITORING_METHODOLOGY_PLAN: true,
          ADDITIONAL_INFORMATION: true,
          DEFINE_MONITORING_APPROACHES: true,
          UNCERTAINTY_ANALYSIS: true,
          CALCULATION_CO2: true,
          CALCULATION_PFC: true,
          MEASUREMENT_N2O: true,
          INHERENT_CO2: true,
          TRANSFERRED_CO2_N2O: true,
          FALLBACK: true,
          MEASUREMENT_CO2: true,
        },
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS'],
      });
      await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(true);
    });

    it('should redirect', async () => {
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          FUELS_AND_EQUIPMENT: {
            type: 'ACCEPTED',
            notes: 'Notes',
            changesRequired: 'Changes',
          },
        },
        reviewSectionsCompleted: {
          CONFIDENTIALITY_STATEMENT: true,
          FUELS_AND_EQUIPMENT: true,
          INSTALLATION_DETAILS: true,
          MANAGEMENT_PROCEDURES: true,
          MONITORING_METHODOLOGY_PLAN: true,
          ADDITIONAL_INFORMATION: true,
          DEFINE_MONITORING_APPROACHES: true,
          UNCERTAINTY_ANALYSIS: true,
          CALCULATION_CO2: true,
          CALCULATION_PFC: true,
          MEASUREMENT_N2O: true,
          INHERENT_CO2: true,
          TRANSFERRED_CO2_N2O: true,
          MEASUREMENT_CO2: true,
        },
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS'],
      });
      await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(
        router.parseUrl(`/`),
      );

      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          FUELS_AND_EQUIPMENT: {
            type: 'OPERATOR_AMENDS_NEEDED',
            notes: 'Notes',
            changesRequired: 'Changes',
          },
        },
        reviewSectionsCompleted: {
          CONFIDENTIALITY_STATEMENT: true,
          FUELS_AND_EQUIPMENT: true,
          INSTALLATION_DETAILS: true,
          MANAGEMENT_PROCEDURES: true,
          MONITORING_METHODOLOGY_PLAN: true,
          ADDITIONAL_INFORMATION: true,
          DEFINE_MONITORING_APPROACHES: true,
          UNCERTAINTY_ANALYSIS: true,
          CALCULATION_CO2: true,
          CALCULATION_PFC: true,
          MEASUREMENT_N2O: true,
          INHERENT_CO2: true,
          TRANSFERRED_CO2_N2O: true,
          FALLBACK: true,
          MEASUREMENT_CO2: true,
        },
      });
      await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(
        router.parseUrl(`/`),
      );

      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          FUELS_AND_EQUIPMENT: {
            type: 'ACCEPTED',
            notes: 'Notes',
          },
        },
        reviewSectionsCompleted: {
          CONFIDENTIALITY_STATEMENT: true,
          FUELS_AND_EQUIPMENT: true,
          INSTALLATION_DETAILS: true,
          MANAGEMENT_PROCEDURES: true,
          MONITORING_METHODOLOGY_PLAN: true,
          ADDITIONAL_INFORMATION: true,
          DEFINE_MONITORING_APPROACHES: true,
          UNCERTAINTY_ANALYSIS: true,
          CALCULATION_CO2: true,
          CALCULATION_PFC: true,
          MEASUREMENT_N2O: true,
          INHERENT_CO2: true,
          TRANSFERRED_CO2_N2O: true,
          FALLBACK: true,
          MEASUREMENT_CO2: true,
        },
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS'],
      });
      await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(
        router.parseUrl(`/`),
      );

      store.setState(mockReviewState);
      await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(
        router.parseUrl(`/`),
      );
    });
  });

  describe('variation request', () => {
    let store: PermitVariationStore;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule],
        providers: [
          ReturnForAmendsGuard,
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      });
      router = TestBed.inject(Router);
      store = TestBed.inject(PermitVariationStore);
      guard = TestBed.inject(ReturnForAmendsGuard);
    });

    it('should activate if variation and about the variation is operator for amend', async () => {
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        allowedRequestTaskActions: [
          'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW',
          'PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS',
        ],
        permitVariationDetailsReviewCompleted: true,
        permitVariationDetailsCompleted: true,
        permitVariationDetailsReviewDecision: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            notes: 'notes',
            requiredChanges: [{ reason: 'reason' }],
          } as any,
        },
      });

      await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(true);
    });

    it('should not activate if variation and about the variation is not operator for amend', async () => {
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        daysRemaining: 13,
        allowedRequestTaskActions: [
          'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW',
          'PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS',
        ],
        permitVariationDetailsReviewCompleted: true,
        permitVariationDetailsCompleted: true,
        permitVariationDetailsReviewDecision: {
          type: 'ACCEPTED',
          details: {
            notes: 'notes',
          } as any,
        },
      });

      await expect(firstValueFrom(guard.canActivate() as Observable<boolean | UrlTree>)).resolves.toEqual(
        router.parseUrl(`/`),
      );
    });
  });
});
