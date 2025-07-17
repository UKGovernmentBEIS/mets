import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { AuthStore } from '@core/store';
import { expectBusinessErrorToBe } from '@error/testing/business-error';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { VerificationGuard } from '@tasks/aer/submit/send-report/verification/verification.guard';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { AccountVerificationBodyService } from 'pmrv-api';

describe('VerificationGuard', () => {
  let guard: VerificationGuard;
  let store: CommonTasksStore;
  let authStore: AuthStore;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  beforeEach(() => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccount: jest.fn(),
    };

    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        KeycloakService,
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
      ],
    });

    guard = TestBed.inject(VerificationGuard);
    store = TestBed.inject(CommonTasksStore);

    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('AVIATION');
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if vb exist', async () => {
    jest.spyOn(store, 'requestInfo$', 'get').mockReturnValue(
      of({
        accountId: 1,
      }),
    );
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 1, name: 'Verifier' }));

    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();
  });

  it('should not activate if vb not exist', async () => {
    jest.spyOn(store, 'requestInfo$', 'get').mockReturnValue(
      of({
        accountId: 1,
      }),
    );
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of(null));

    await expect(lastValueFrom(guard.canActivate())).rejects.toBeTruthy();
    await expectBusinessErrorToBe(notFoundVerificationBodyError());
  });
});
