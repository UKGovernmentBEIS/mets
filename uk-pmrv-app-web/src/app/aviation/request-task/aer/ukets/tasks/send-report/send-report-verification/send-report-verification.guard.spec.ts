import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { lastValueFrom, of } from 'rxjs';

import { RequestTaskState, RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { AuthStore } from '@core/store';
import { expectBusinessErrorToBe } from '@error/testing/business-error';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';

import { AccountVerificationBodyService } from 'pmrv-api';

import { mockState } from '../testing/mock-state';
import { VerificationGuard } from './send-report-verification.guard';

describe('VerificationGuard', () => {
  let guard: VerificationGuard;
  let store: RequestTaskStore;
  let authStore: AuthStore;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  beforeEach(async () => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccount: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      ...mockState,
    } as RequestTaskState);

    guard = TestBed.inject(VerificationGuard);

    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('AVIATION');
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if vb exist', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 1, name: 'Verifier' }));
    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();
  });

  it('should not activate if vb not exist', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of(null));

    await expect(lastValueFrom(guard.canActivate())).rejects.toBeTruthy();
    await expectBusinessErrorToBe(notFoundVerificationBodyError());
  });
});
