import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { lastValueFrom, of } from 'rxjs';

import { expectBusinessErrorToBe } from '@error/testing/business-error';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { BdrService } from '@tasks/bdr/shared';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AccountVerificationBodyService } from 'pmrv-api';

import { SendReportVerifierGuard } from './send-report-verifier-guard';

describe('SendReportVerifierGuard', () => {
  let guard: SendReportVerifierGuard;
  let store: CommonTasksStore;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  beforeEach(() => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccount: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        BdrService,
        ItemNamePipe,
        provideHttpClient(withInterceptorsFromDi()),
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
        CapitalizeFirstPipe,
      ],
    });

    guard = TestBed.inject(SendReportVerifierGuard);
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

    await expect(lastValueFrom(guard.canActivate())).rejects.toBeTruthy();
    await expectBusinessErrorToBe(notFoundVerificationBodyError());
  });
});
