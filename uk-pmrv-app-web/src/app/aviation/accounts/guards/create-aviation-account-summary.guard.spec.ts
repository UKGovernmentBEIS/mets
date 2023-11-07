import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';

import { firstValueFrom } from 'rxjs';

import { mockClass } from '@testing';

import { AviationAccountReportingStatusService, AviationAccountsService, AviationAccountUpdateService } from 'pmrv-api';

import { AviationAccountsStore } from '../store';
import { CreateAviationAccountSummaryGuard } from './create-aviation-account-summary.guard';

describe('CreateAviationAccountSummaryGuard', () => {
  let guard: CreateAviationAccountSummaryGuard;
  let store: AviationAccountsStore;

  const accountsService = mockClass(AviationAccountsService);
  const router = mockClass(Router);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AviationAccountsStore,
        CreateAviationAccountSummaryGuard,
        { provide: Router, useValue: router },
        { provide: AviationAccountsService, useValue: accountsService },
        { provide: AviationAccountReportingStatusService, useValue: mockClass(AviationAccountReportingStatusService) },
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
      ],
    });

    store = TestBed.inject(AviationAccountsStore);
    guard = TestBed.inject(CreateAviationAccountSummaryGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow route when form is initially submitted', async () => {
    store.setIsInitiallySubmitted(true);
    const allow = await firstValueFrom(guard.canActivate());
    expect(allow).toBe(true);
  });

  it('should navigate to create account form page when is not initially submitted', async () => {
    store.setIsInitiallySubmitted(false);
    router.parseUrl.mockReturnValueOnce(new UrlTree());
    const allow = await firstValueFrom(guard.canActivate());
    expect(router.parseUrl).toHaveBeenCalledWith('/aviation/accounts/create');
    expect(allow).toBeInstanceOf(UrlTree);
  });
});
