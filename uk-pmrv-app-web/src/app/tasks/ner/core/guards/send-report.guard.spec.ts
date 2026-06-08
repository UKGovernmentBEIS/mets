import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot, UrlTree } from '@angular/router';

import { firstValueFrom, lastValueFrom, Observable, of } from 'rxjs';

import { expectBusinessErrorToBe } from '@error/testing/business-error';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { mockNerSubmitPayloadCompleted, mockNerSubmitStateBuild } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AccountVerificationBodyService } from 'pmrv-api';

import { NerService } from '../ner.service';
import { nerSendReportGuard } from './send-report.guard';

describe('sendReportGuard', () => {
  let store: CommonTasksStore;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => nerSendReportGuard(...guardParameters));

  beforeEach(() => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccount: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        NerService,
        ItemNamePipe,
        provideHttpClient(withInterceptorsFromDi()),
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
        CapitalizeFirstPipe,
      ],
    });

    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });

  it('should activate when verification performed true (send to regulator)', async () => {
    store.setState(
      mockNerSubmitStateBuild({
        ...mockNerSubmitPayloadCompleted,
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
      firstValueFrom(executeGuard(activatedRouteSnapshot, {} as RouterStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should activate if verifier exist and verification performed false (send to verifier)', async () => {
    store.setState(
      mockNerSubmitStateBuild({
        ...mockNerSubmitPayloadCompleted,
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
      firstValueFrom(executeGuard(activatedRouteSnapshot, {} as RouterStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate if vb not exist and verification performed false (send to verifier)', async () => {
    store.setState(
      mockNerSubmitStateBuild({
        ...mockNerSubmitPayloadCompleted,
        verificationPerformed: false,
      }),
    );

    jest.spyOn(store, 'requestInfo$', 'get').mockReturnValue(
      of({
        accountId: 1,
      }),
    );
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of(null));

    await expect(
      lastValueFrom(executeGuard(activatedRouteSnapshot, {} as RouterStateSnapshot) as Observable<true | UrlTree>),
    ).rejects.toBeTruthy();
    await expectBusinessErrorToBe(notFoundVerificationBodyError());
  });
});
