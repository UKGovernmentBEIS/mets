import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { firstValueFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AuthStore } from '@core/store';
import { mockClass } from '@testing';

import { AviationAccountReportingStatusService, AviationAccountsService, AviationAccountUpdateService } from 'pmrv-api';

import { AviationAccountsStore } from '../store';
import { AviationAccountReportingStatusHistoryGuard } from './aviation-account-reporting-status-history.guard';

let guard: AviationAccountReportingStatusHistoryGuard;

describe('AviationAccountReportingStatusHistoryGuard -> canActivate', () => {
  let authStore: AuthStore;

  const accountsService = mockClass(AviationAccountsService);
  const pendingRequestService = mockClass(PendingRequestService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        AviationAccountsStore,
        AviationAccountReportingStatusHistoryGuard,
        { provide: AviationAccountsService, useValue: accountsService },
        { provide: AviationAccountReportingStatusService, useValue: mockClass(AviationAccountReportingStatusService) },
        { provide: PendingRequestService, useValue: pendingRequestService },
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    guard = TestBed.inject(AviationAccountReportingStatusHistoryGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow only for regulators', async () => {
    authStore.setUserState({ roleType: 'REGULATOR' });
    const allowReg = await firstValueFrom(guard.canActivate());
    expect(allowReg).toEqual(true);

    authStore.setUserState({ roleType: 'OPERATOR' });
    const allowOp = await firstValueFrom(guard.canActivate());
    expect(allowOp).toEqual(false);
  });
});

describe('AviationAccountReportingStatusHistoryGuard -> canDeactivate', () => {
  const store: Partial<AviationAccountsStore> = {
    resetReportingStatusHistory: jest.fn(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [AviationAccountReportingStatusHistoryGuard, { provide: AviationAccountsStore, useValue: store }],
    });

    guard = TestBed.inject(AviationAccountReportingStatusHistoryGuard);
  });

  it('should reset store and destroy form on deactivate', () => {
    const resetSpy = jest.spyOn(store, 'resetReportingStatusHistory');
    guard.canDeactivate();
    expect(resetSpy).toHaveBeenCalled();
  });
});
