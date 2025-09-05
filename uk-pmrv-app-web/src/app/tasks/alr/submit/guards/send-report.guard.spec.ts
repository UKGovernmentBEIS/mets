import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, UrlTree } from '@angular/router';

import { firstValueFrom, lastValueFrom, Observable, of } from 'rxjs';

import { expectBusinessErrorToBe } from '@error/testing/business-error';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { AlrService } from '@tasks/alr/core';
import { mockALRApplicationSubmitPayloadCompleted, mockAlrStateBuild } from '@tasks/alr/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AccountVerificationBodyService } from 'pmrv-api';

import { AlrSendReportGuard } from './send-report.guard';

describe('AlrSendReportGuard', () => {
  let guard: AlrSendReportGuard;
  let store: CommonTasksStore;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccount: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        AlrService,
        ItemNamePipe,
        provideHttpClient(withInterceptorsFromDi()),
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
        AlrSendReportGuard,
        CapitalizeFirstPipe,
      ],
    });

    guard = TestBed.inject(AlrSendReportGuard);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate when verification performed true (send to regulator)', async () => {
    store.setState(
      mockAlrStateBuild({
        ...mockALRApplicationSubmitPayloadCompleted,
        verificationPerformed: true,
      }),
    );
    jest.spyOn(store, 'requestInfo$', 'get').mockReturnValue(
      of({
        accountId: 1,
      }),
    );
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 1, name: 'Verifier' }));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should activate if verifier exist and verification performed false (send to verifier)', async () => {
    store.setState(
      mockAlrStateBuild({
        ...mockALRApplicationSubmitPayloadCompleted,
        verificationPerformed: false,
      }),
    );

    jest.spyOn(store, 'requestInfo$', 'get').mockReturnValue(
      of({
        accountId: 1,
      }),
    );
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 1, name: 'Verifier' }));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate if vb not exist and verification performed false (send to verifier)', async () => {
    store.setState(
      mockAlrStateBuild({
        ...mockALRApplicationSubmitPayloadCompleted,
        verificationPerformed: false,
      }),
    );

    jest.spyOn(store, 'requestInfo$', 'get').mockReturnValue(
      of({
        accountId: 1,
      }),
    );
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of(null));

    await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot))).rejects.toBeTruthy();
    await expectBusinessErrorToBe(notFoundVerificationBodyError());
  });
});
