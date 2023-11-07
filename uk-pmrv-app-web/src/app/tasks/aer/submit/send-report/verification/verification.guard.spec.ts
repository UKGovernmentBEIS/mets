import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { VerificationGuard } from '@tasks/aer/submit/send-report/verification/verification.guard';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AccountVerificationBodyService } from 'pmrv-api';

import { BusinessErrorService } from '../../../../../error/business-error/business-error.service';

describe('VerificationGuard', () => {
  let guard: VerificationGuard;
  let store: CommonTasksStore;
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
        { provide: BusinessErrorService, useValue: mockClass(BusinessErrorService) },
      ],
    });

    guard = TestBed.inject(VerificationGuard);
    store = TestBed.inject(CommonTasksStore);
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

    await expect(lastValueFrom(guard.canActivate())).resolves.toBeFalsy();
  });
});
