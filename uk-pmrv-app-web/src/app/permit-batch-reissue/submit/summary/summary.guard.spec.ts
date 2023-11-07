import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { initialState } from '../store/permit-batch-reissue.state';
import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';
import { mockSubmitCompletedState } from '../testing/mock-data';
import { SummaryGuard } from './summary.guard';

describe('SummaryGuard', () => {
  let guard: SummaryGuard;
  let router: Router;
  let store: PermitBatchReissueStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();

  const routerStateSnapshot = {
    url: '/batch-variations/submit/summary',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(SummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitBatchReissueStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to filters if filters step is not completed', async () => {
    store.setState({
      ...initialState,
      accountStatuses: undefined,
      emitterTypes: undefined,
      installationCategories: undefined,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/batch-variations/submit/filters`));
  });

  it('should redirect to filters if signatory step is not completed', async () => {
    store.setState({
      ...initialState,
      ...mockSubmitCompletedState,
      signatory: null,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/batch-variations/submit/filters`));
  });

  it('should activate when all wizard steps are completed', async () => {
    store.setState({
      ...initialState,
      ...mockSubmitCompletedState,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
