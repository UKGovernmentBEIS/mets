import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
} from '../../../../permit-variation/testing/mock';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockReviewState, mockReviewStateBuild } from '../../../testing/mock-state';
import { EmissionsGuard } from './emissions.guard';

describe('EmissionsGuard', () => {
  let guard: EmissionsGuard;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: mockReviewState.requestTaskId };
  activatedRouteSnapshot.data = {
    statusKey: 'determination',
  };

  describe('permit issuance', () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, HttpClientTestingModule],
        providers: [
          EmissionsGuard,
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      });
      guard = TestBed.inject(EmissionsGuard);
      router = TestBed.inject(Router);
      store = TestBed.inject(PermitApplicationStore);
    });

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should activate if task is changing', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
      await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
    });

    it('should redirect to summary if task is complete and not changing', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockReviewStateBuild(
          {
            type: 'GRANTED',
            reason: 'reason',
            officialNotice: 'official notice',
            annualEmissionsTargets: {
              '2021': 55,
              '2022': 3,
            },
          },
          {
            determination: true,
          },
        ),
        permitType: 'HSE',
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-issuance/237/review/determination/summary`));
    });

    it('should redirect to answers if task is not  complete and not changing', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockReviewStateBuild(
          {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: '1-1-2030',
            annualEmissionsTargets: {
              '2021': 55,
              '2022': 3,
            },
          },
          {
            determination: false,
          },
        ),
        permitType: 'HSE',
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-issuance/237/review/determination/answers`));
    });

    it('should  allow for HSE', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockReviewStateBuild(
          {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: '1-1-2030',
          },
          {
            determination: false,
          },
        ),
        permitType: 'HSE',
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });
  });

  describe('permit variation', () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [RouterTestingModule, HttpClientTestingModule],
        providers: [
          EmissionsGuard,
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      });
      guard = TestBed.inject(EmissionsGuard);
      router = TestBed.inject(Router);
      store = TestBed.inject(PermitApplicationStore);
    });

    it('should redirect to answers if variation task and granted and not completed and all wizard filled', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        permitType: 'HSE',
        determination: {
          type: 'GRANTED',
          reason: 'reason',
          activationDate: '1-1-2030',
          annualEmissionsTargets: {
            '2021': 55,
            '2022': 3,
          },
          logChanges: 'klklkllkllk',
        },
        reviewSectionsCompleted: { determination: false },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-variation/237/review/determination/answers`));
    });

    it('should return true if variation task and granted and emissions not exist', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        permitType: 'HSE',
        determination: {
          type: 'GRANTED',
          reason: 'reason',
          activationDate: '1-1-2030',
        },
        reviewSectionsCompleted: { determination: false },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to answers if variation regulator led task and not completed and all wizard filled', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      (store as PermitVariationStore).setState({
        ...mockPermitVariationRegulatorLedPayload,
        permitType: 'HSE',
        permitVariationDetails: {
          reason: 'reason',
          modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
        },
        permitVariationDetailsCompleted: true,
        determination: {
          reason: 'reason',
          activationDate: '2023-01-01',
          annualEmissionsTargets: {
            '2021': 55,
            '2022': 3,
          },
          reasonTemplate: 'WHERE_OPERATOR_FAILED_TO_APPLY_IN_ACCORDANCE_WITH_CONDITIONS',
          logChanges: 'logChanges',
        },
        reviewSectionsCompleted: { determination: false },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-variation/237/review/determination/answers`));
    });
  });
});
