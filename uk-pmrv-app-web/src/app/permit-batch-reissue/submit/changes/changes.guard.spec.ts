import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { initialState } from '../store/permit-batch-reissue.state';
import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';
import { ChangesGuard } from './changes.guard';

describe('ChangesGuard', () => {
  let guard: ChangesGuard;
  let router: Router;
  let store: PermitBatchReissueStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();

  const routerStateSnapshot = {
    url: '/batch-variations/submit/changes',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(ChangesGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitBatchReissueStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to filters if previous step not completed', async () => {
    store.setState({
      ...initialState,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/batch-variations/submit/filters`));
  });

  it('should activate when previous step completed', async () => {
    store.setState({
      ...initialState,
      accountStatuses: ['AWAITING_REVOCATION'],
      emitterTypes: ['GHGE'],
      installationCategories: ['A_LOW_EMITTER'],
      changesDetails: {
        changesSummary: 'summary',
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
