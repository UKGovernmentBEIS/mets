import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../permit-variation/store/permit-variation.store';
import { mockPermitVariationSubmitOperatorLedPayload } from '../../../permit-variation/testing/mock';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { AmendSummaryGuard } from './amend-summary.guard';

describe('AmendSummaryGuard', () => {
  let guard: AmendSummaryGuard;

  describe('issuance request', () => {
    let store: PermitIssuanceStore;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        providers: [
          AmendSummaryGuard,
          { provide: TasksService, useValue: mockClass(TasksService) },
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      });
      store = TestBed.inject(PermitIssuanceStore);
      guard = TestBed.inject(AmendSummaryGuard);
    });

    it('should not activate if status is not complete', async () => {
      store.setState({
        ...mockState,
        reviewGroupDecisions: {
          FUELS_AND_EQUIPMENT: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes required for installation details',
            notes: 'notes',
          },
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'fuels' }))),
      ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-issuance/1/amend/fuels'));
    });

    it('should activate if status is complete', async () => {
      store.setState({
        ...mockState,
        reviewGroupDecisions: {
          FUELS_AND_EQUIPMENT: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes required for installation details',
            notes: 'notes',
          },
        },
        permitSectionsCompleted: {
          ...mockState.permitSectionsCompleted,
          AMEND_fuels: [true],
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'fuels' }))),
      ).resolves.toEqual(true);
    });
  });

  describe('variation request', () => {
    let store: PermitVariationStore;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        providers: [
          AmendSummaryGuard,
          { provide: TasksService, useValue: mockClass(TasksService) },
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      });
      store = TestBed.inject(PermitVariationStore);
      guard = TestBed.inject(AmendSummaryGuard);
    });

    it('should not activate if variation and about the variation status is not complete', async () => {
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        permitVariationDetailsReviewDecision: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            notes: 'notes',
            requiredChanges: [{ reason: 'reason' }],
          } as any,
        },
        permitVariationDetailsAmendCompleted: false,
        payloadType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
        requestTaskType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
      });

      await expect(
        firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'about-variation' }))),
      ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-variation/1/amend/about-variation'));
    });

    it('should activate if variation and about the variation status is complete', async () => {
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        permitVariationDetailsReviewDecision: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            notes: 'notes',
            requiredChanges: [{ reason: 'reason' }],
          } as any,
        },
        permitVariationDetailsAmendCompleted: true,
        payloadType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
        requestTaskType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
      });

      await expect(
        firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'about-variation' }))),
      ).resolves.toEqual(true);
    });
  });
});
