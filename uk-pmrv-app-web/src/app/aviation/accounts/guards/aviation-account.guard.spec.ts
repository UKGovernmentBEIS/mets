import { TestBed } from '@angular/core/testing';

import { lastValueFrom, of } from 'rxjs';

import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import {
  AviationAccountReportingStatusService,
  AviationAccountsService,
  AviationAccountUpdateService,
  AviationAccountViewService,
} from 'pmrv-api';

import { AviationAccountsStore } from '../store';
import { mockedAccount } from '../testing/mock-data';
import { AviationAccountGuard } from './aviation-account.guard';

let guard: AviationAccountGuard;

describe('AviationAccountGuard', () => {
  const accountsService = mockClass(AviationAccountViewService);

  beforeEach(() => {
    accountsService.getAviationAccountById.mockReturnValueOnce(of(mockedAccount));

    TestBed.configureTestingModule({
      providers: [
        AviationAccountsStore,
        AviationAccountGuard,
        { provide: AviationAccountViewService, useValue: accountsService },
        { provide: AviationAccountsService, useValue: mockClass(AviationAccountsService) },
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
        { provide: AviationAccountReportingStatusService, useValue: mockClass(AviationAccountReportingStatusService) },
      ],
    });

    guard = TestBed.inject(AviationAccountGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should check account existance', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1' }))),
    ).resolves.toBeTruthy();

    expect(accountsService.getAviationAccountById).toHaveBeenCalledWith(1);
  });
});
