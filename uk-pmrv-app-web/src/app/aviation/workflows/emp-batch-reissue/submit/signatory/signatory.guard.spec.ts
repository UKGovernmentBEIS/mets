import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { EmpBatchReissueStore } from '@aviation/workflows/emp-batch-reissue/submit/store/emp-batch-reissue.store';

import { initialState } from '../store/emp-batch-reissue.state';
import { SignatoryGuard } from './signatory.guard';

describe('SignatoryGuard', () => {
  let guard: SignatoryGuard;
  let router: Router;
  let store: EmpBatchReissueStore;

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
    store = TestBed.inject(EmpBatchReissueStore);
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
      reportingStatuses: ['EXEMPT_COMMERCIAL'],
      emissionTradingSchemes: ['UK_ETS_AVIATION'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
