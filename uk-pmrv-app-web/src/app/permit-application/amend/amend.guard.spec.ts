import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../permit-variation/store/permit-variation.store';
import { mockPermitVariationSubmitOperatorLedPayload } from '../../permit-variation/testing/mock';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockState } from '../testing/mock-state';
import { AmendGuard } from './amend.guard';

describe('AmendGuard', () => {
  let guard: AmendGuard;

  describe('issuance request', () => {
    let store: PermitIssuanceStore;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        providers: [
          AmendGuard,
          { provide: TasksService, useValue: mockClass(TasksService) },
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      });
      store = TestBed.inject(PermitIssuanceStore);
      guard = TestBed.inject(AmendGuard);
    });

    it('should not activate if section is not available for amends', async () => {
      store.setState({
        ...mockState,
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes required for installation details',
            notes: 'notes',
          },
          ADDITIONAL_INFORMATION: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes required for additional information',
            notes: 'notes',
          },
        },
        permitSectionsCompleted: {
          ...mockState.permitSectionsCompleted,
          AMEND_details: [true],
        },
      });
      await expect(
        firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'fuels' }))),
      ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-issuance/1'));
    });

    it('should not activate if amend task has already been completed', async () => {
      store.setState({
        ...mockState,
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes required for installation details',
            notes: 'notes',
          },
          ADDITIONAL_INFORMATION: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes required for additional information',
            notes: 'notes',
          },
        },
        permitSectionsCompleted: {
          ...mockState.permitSectionsCompleted,
          AMEND_details: [true],
        },
      });
      await expect(
        firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'details' }))),
      ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-issuance/1'));
    });

    it('should activate if section is available for amends and task has not been completed', async () => {
      store.setState({
        ...mockState,
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes required for installation details',
            notes: 'notes',
          },
          ADDITIONAL_INFORMATION: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes required for additional information',
            notes: 'notes',
          },
        },
        permitSectionsCompleted: {
          ...mockState.permitSectionsCompleted,
          AMEND_details: [true],
        },
      });
      await expect(
        firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'additional-info' }))),
      ).resolves.toEqual(true);
    });
  });

  describe('variation request', () => {
    let store: PermitVariationStore;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule],
        providers: [
          AmendGuard,
          { provide: TasksService, useValue: mockClass(TasksService) },
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      });
      store = TestBed.inject(PermitVariationStore);
      guard = TestBed.inject(AmendGuard);
    });

    it('should not activate if variation and amend task has already been completed', async () => {
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        payloadType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
        requestTaskType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
        permitVariationDetailsReviewDecision: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            notes: 'notes',
            requiredChanges: [{ reason: 'reason' }],
          } as any,
        },
        permitVariationDetailsAmendCompleted: true,
      });
      await expect(
        firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'about-variation' }))),
      ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-variation/1'));
    });

    it('should activate if variation and amend task is not completed', async () => {
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        payloadType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
        requestTaskType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
        permitVariationDetailsReviewDecision: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            notes: 'notes',
            requiredChanges: [{ reason: 'reason' }],
          } as any,
        },
      });
      await expect(
        firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1', section: 'about-variation' }))),
      ).resolves.toEqual(true);
    });
  });
});
