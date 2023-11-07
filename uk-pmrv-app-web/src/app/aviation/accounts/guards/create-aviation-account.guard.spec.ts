import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { firstValueFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AuthStore } from '@core/store';
import { mockClass } from '@testing';

import { AviationAccountReportingStatusService, AviationAccountsService, AviationAccountUpdateService } from 'pmrv-api';

import { AviationAccountFormProvider } from '../services';
import { AviationAccountsStore } from '../store';
import { CreateAviationAccountGuard } from './create-aviation-account.guard';

let guard: CreateAviationAccountGuard;

describe('CreateAviationAccountGuard -> canActivate', () => {
  let authStore: AuthStore;

  const accountsService = mockClass(AviationAccountsService);
  const pendingRequestService = mockClass(PendingRequestService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        AviationAccountsStore,
        AviationAccountFormProvider,
        CreateAviationAccountGuard,
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
        { provide: AviationAccountsService, useValue: accountsService },
        { provide: AviationAccountReportingStatusService, useValue: mockClass(AviationAccountReportingStatusService) },
        { provide: PendingRequestService, useValue: pendingRequestService },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    guard = TestBed.inject(CreateAviationAccountGuard);
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

describe('CreateAviationAccountGuard -> canDeactivate', () => {
  const store: Partial<AviationAccountsStore> = {
    resetCreateAccount: jest.fn(),
  };
  const formProvider: Partial<AviationAccountFormProvider> = {
    destroyForm: jest.fn(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        CreateAviationAccountGuard,
        { provide: AviationAccountsStore, useValue: store },
        { provide: AviationAccountFormProvider, useValue: formProvider },
      ],
    });

    guard = TestBed.inject(CreateAviationAccountGuard);
  });

  it('should reset store and destroy form on deactivate', () => {
    const resetSpy = jest.spyOn(store, 'resetCreateAccount');
    guard.canDeactivate();
    expect(resetSpy).toHaveBeenCalled();
    expect(formProvider.destroyForm).toHaveBeenCalled();
  });
});
