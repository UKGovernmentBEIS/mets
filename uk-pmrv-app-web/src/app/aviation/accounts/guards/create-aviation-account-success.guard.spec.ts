import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';

import { mockClass, MockType } from '@testing';

import { AviationAccountReportingStatusService, AviationAccountsService, AviationAccountUpdateService } from 'pmrv-api';

import { AviationAccountsStore } from '../store';
import { CreateAviationAccountSuccessGuard } from './create-aviation-account-success.guard';

describe('CreateAviationAccountSuccessGuard', () => {
  let guard: CreateAviationAccountSuccessGuard;
  let router: MockType<Router>;
  let store: AviationAccountsStore;

  const accountsService = mockClass(AviationAccountsService);

  beforeEach(() => {
    router = mockClass(Router);
    router.parseUrl.mockReturnValue(new UrlTree());

    TestBed.configureTestingModule({
      providers: [
        AviationAccountsStore,
        CreateAviationAccountSuccessGuard,
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
        { provide: AviationAccountsService, useValue: accountsService },
        { provide: AviationAccountReportingStatusService, useValue: mockClass(AviationAccountReportingStatusService) },
        { provide: Router, useValue: router },
      ],
    });

    guard = TestBed.inject(CreateAviationAccountSuccessGuard);
    store = TestBed.inject(AviationAccountsStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow route when form is submitted', (done) => {
    store.setIsInitiallySubmitted(true);
    store.setIsSubmitted(true);
    store.setNewAccount(null);

    guard.canActivate().subscribe((val) => {
      expect(val).toBe(true);
      done();
    });
  });

  it('should redirect to account form when is initially submitted', (done) => {
    store.setIsInitiallySubmitted(true);
    store.setIsSubmitted(false);
    store.setNewAccount({
      name: 'TEST',
      emissionTradingScheme: 'UK_ETS_AVIATION',
      crcoCode: 'TEST',
      commencementDate: 'TEST',
    });

    guard.canActivate().subscribe((val) => {
      expect(router.parseUrl).toHaveBeenCalledWith('/aviation/accounts/create');
      expect(val).toBeInstanceOf(UrlTree);
      done();
    });
  });

  it('should redirect to dashboard when no submission', (done) => {
    store.setIsInitiallySubmitted(false);
    store.setIsSubmitted(false);
    store.setNewAccount(null);

    guard.canActivate().subscribe((val) => {
      expect(router.parseUrl).toHaveBeenCalledWith('/aviation/dashboard');
      expect(val).toBeInstanceOf(UrlTree);
      done();
    });
  });
});
