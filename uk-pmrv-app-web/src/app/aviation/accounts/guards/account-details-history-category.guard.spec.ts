import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { firstValueFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AuthStore } from '@core/store';
import { mockClass } from '@testing';

import { AccountDetailsHistoryService, AviationAccountsService, AviationAccountUpdateService } from 'pmrv-api';

import { AviationAccountsStore } from '../store';
import { AviationAccountDetailsHistoryGuard } from './account-details-history-category.guard';

let guard: AviationAccountDetailsHistoryGuard;
describe('AviationAccountDetailsHistoryGuard -> canActivate', () => {
  let authStore: AuthStore;

  const accountsService = mockClass(AviationAccountsService);
  const pendingRequestService = mockClass(PendingRequestService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        provideHttpClient(),
        AviationAccountsStore,
        AviationAccountDetailsHistoryGuard,
        { provide: AviationAccountsService, useValue: accountsService },
        { provide: AccountDetailsHistoryService, useValue: mockClass(AccountDetailsHistoryService) },
        { provide: PendingRequestService, useValue: pendingRequestService },
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    guard = TestBed.inject(AviationAccountDetailsHistoryGuard);
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

describe('AviationAccountDetailsHistoryGuard -> canDeactivate', () => {
  const store: Partial<AviationAccountsStore> = {
    resetAccountDetailsHistory: jest.fn(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [AviationAccountDetailsHistoryGuard, { provide: AviationAccountsStore, useValue: store }],
    });

    guard = TestBed.inject(AviationAccountDetailsHistoryGuard);
  });

  it('should reset store and destroy form on deactivate', () => {
    const resetSpy = jest.spyOn(store, 'resetAccountDetailsHistory');
    guard.canDeactivate();
    expect(resetSpy).toHaveBeenCalled();
  });
});
