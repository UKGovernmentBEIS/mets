import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitVariationStore } from '../../store/permit-variation.store';
import { mockPermitVariationSubmitOperatorLedPayload } from '../../testing/mock';
import { ChangesGuard } from './changes.guard';

describe('ChangesGuard', () => {
  let store: PermitVariationStore;
  let guard: ChangesGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: mockPermitVariationSubmitOperatorLedPayload.requestTaskId };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [RouterTestingModule, HttpClientTestingModule] });

    guard = TestBed.inject(ChangesGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitVariationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if status is completed', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    store.setState({
      ...mockPermitVariationSubmitOperatorLedPayload,
      permitVariationDetails: null,
      permitVariationDetailsCompleted: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-variation/237/about/summary'));
  });

  it('should redirect to answers if wizard is completed', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    store.setState({
      ...mockPermitVariationSubmitOperatorLedPayload,
      permitVariationDetails: {
        reason: 'Reason',
        modifications: [
          {
            type: 'INSTALLATION_NAME',
          },
          {
            type: 'NEW_SOURCE_STREAMS',
          },
          {
            type: 'DEFAULT_VALUE_OR_ESTIMATION_METHOD',
          },
        ],
      },
      permitVariationDetailsCompleted: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-variation/237/about/answers'));
  });

  it('should redirect to about if not started', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    store.setState({
      ...mockPermitVariationSubmitOperatorLedPayload,
      permitVariationDetails: null,
      permitVariationDetailsCompleted: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-variation/237/about'));
  });

  it('should allow if reason is filled', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    store.setState({
      ...mockPermitVariationSubmitOperatorLedPayload,
      permitVariationDetails: { reason: 'Reason', modifications: [{ type: 'INSTALLATION_NAME' }] },
      permitVariationDetailsCompleted: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-variation/237/about/answers'));
  });
});
