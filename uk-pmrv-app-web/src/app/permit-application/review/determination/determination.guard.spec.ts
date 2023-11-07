import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
} from '../../../permit-variation/testing/mock';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewRequestActionState, mockReviewState, mockReviewStateBuild } from '../../testing/mock-state';
import { DeterminationGuard } from './determination.guard';

describe('DeterminationGuard', () => {
  let guard: DeterminationGuard;
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
          DeterminationGuard,
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      });
      guard = TestBed.inject(DeterminationGuard);
      router = TestBed.inject(Router);
      store = TestBed.inject(PermitApplicationStore);
    });

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should activate if task is not started ', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState(mockReviewStateBuild());

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should activate if task is changing', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
      await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
    });

    it('should activate if task is in progress and not changing', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState(mockReviewStateBuild());

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to answers if task needs review and not changing ', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState(
        mockReviewStateBuild(
          {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: '1-1-2030',
          },
          {
            determination: false,
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-issuance/237/review/determination/answers`));
    });

    it('should redirect to summary if task is complete and not changing', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState(
        mockReviewStateBuild(
          {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: '1-1-2030',
          },
          {
            determination: true,
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-issuance/237/review/determination/summary`));
    });

    it('should activate if action', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockReviewRequestActionState,
        requestActionType: 'PERMIT_ISSUANCE_APPLICATION_GRANTED',
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
          DeterminationGuard,
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      });
      guard = TestBed.inject(DeterminationGuard);
      router = TestBed.inject(Router);
      store = TestBed.inject(PermitApplicationStore);
    });

    it('should redirect to answers if variation operator led task and granted and not completed and log changes exist', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        determination: {
          type: 'GRANTED',
          reason: 'reason',
          activationDate: '1-1-2030',
          logChanges: 'klklkllkllk',
        },
        reviewSectionsCompleted: { determination: false },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-variation/237/review/determination/answers`));
    });

    it('should return true if variation operator led task and granted and not completed and log changes not exist', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        determination: {
          type: 'GRANTED',
          reason: 'reason',
          activationDate: '1-1-2030',
          logChanges: null,
        },
        reviewSectionsCompleted: { determination: false },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to reason if variation regulator led', async () => {
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-variation/237/review/determination/reason`));
    });
  });
});
