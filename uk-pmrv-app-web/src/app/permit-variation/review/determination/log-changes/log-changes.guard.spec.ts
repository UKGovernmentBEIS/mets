import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { mockReviewState } from '../../../../permit-application/testing/mock-state';
import { PermitVariationStore } from '../../../store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
} from '../../../testing/mock';
import { LogChangesGuard } from './log-changes.guard';

describe('LogChangesGuard', () => {
  let guard: LogChangesGuard;
  let router: Router;
  let store: PermitVariationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: mockReviewState.requestTaskId };
  activatedRouteSnapshot.data = {
    statusKey: 'determination',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(LogChangesGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitVariationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if task is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should activate if task is in progress and not changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPermitVariationReviewOperatorLedPayload,
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

  it('should redirect to determination if reason is missing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPermitVariationReviewOperatorLedPayload,
      determination: {
        type: 'GRANTED',
      },
      reviewSectionsCompleted: { determination: false },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-variation/237/review/determination`));
  });

  it('should redirect to determination if activation date is missing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPermitVariationReviewOperatorLedPayload,
      determination: {
        type: 'GRANTED',
        reason: 'reason',
      },
      reviewSectionsCompleted: { determination: false },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-variation/237/review/determination`));
  });

  it('should redirect to answers if not completed and all wizard filled', async () => {
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

  it('should return true if log changes not filled yet', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPermitVariationReviewOperatorLedPayload,
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

  it('should redirect to answers if regulator led and determination not completed and all wizard filled', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPermitVariationRegulatorLedPayload,
      permitVariationDetails: {
        reason: 'reason',
        modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
      },
      permitVariationDetailsCompleted: true,
      determination: {
        reason: 'reason',
        activationDate: '2023-01-01',
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
