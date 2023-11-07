import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { initialState } from '../store/permit-batch-reissue.state';
import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';
import { SignatoryGuard } from './signatory.guard';

describe('SignatoryGuard', () => {
  let guard: SignatoryGuard;
  let router: Router;
  let store: PermitBatchReissueStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();

  const routerStateSnapshot = {
    url: '/batch-variations/submit/signatory',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(SignatoryGuard);
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
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
