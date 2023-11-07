import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitTransferStore } from '../store/permit-transfer.store';
import { mockPermitTransferSubmitPayload } from '../testing/mock';
import { TransferDetailsGuard } from './transfer-details.guard';

describe('TransferDetailsGuard', () => {
  let guard: TransferDetailsGuard;
  let router: Router;
  let store: PermitTransferStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 13 };

  const routerStateSnapshot = (page?: string) => {
    return {
      url: `/permit-transfer/${activatedRouteSnapshot.params.taskId}/transfer-details${page ? `/${page}` : ''}`,
    } as RouterStateSnapshot;
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [HttpClient, HttpHandler],
    });
    guard = TestBed.inject(TransferDetailsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitTransferStore);
    store.setState({ ...mockPermitTransferSubmitPayload });
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if forced to change', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValueOnce({ extras: { state: { changing: true } } } as any);

    expect(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot('summary'))).toEqual(true);
  });

  it('should navigate to transfer details page', async () => {
    await expect(
      firstValueFrom(
        guard.canActivate(activatedRouteSnapshot, routerStateSnapshot('summary')) as Observable<true | UrlTree>,
      ),
    ).resolves.toEqual(router.parseUrl(`/permit-transfer/${activatedRouteSnapshot.params.taskId}/transfer-details`));
  });

  it('should navigate to summary page', async () => {
    store.setState({
      ...store.getState(),
      permitSectionsCompleted: { ...store.getState().permitSectionsCompleted, transferDetails: [true] },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot()) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/permit-transfer/${activatedRouteSnapshot.params.taskId}/transfer-details/summary`),
    );
  });

  it('should return true', async () => {
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot()) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState({
      ...store.getState(),
      permitSectionsCompleted: { ...store.getState().permitSectionsCompleted, transferDetails: [true] },
    });

    await expect(
      firstValueFrom(
        guard.canActivate(activatedRouteSnapshot, routerStateSnapshot('summary')) as Observable<true | UrlTree>,
      ),
    ).resolves.toEqual(true);

    store.setState({
      ...mockPermitTransferSubmitPayload,
      isRequestTask: false,
      permitSectionsCompleted: { ...store.getState().permitSectionsCompleted, transferDetails: [false] },
    });

    await expect(
      firstValueFrom(
        guard.canActivate(activatedRouteSnapshot, routerStateSnapshot('summary')) as Observable<true | UrlTree>,
      ),
    ).resolves.toEqual(true);
  });
});
