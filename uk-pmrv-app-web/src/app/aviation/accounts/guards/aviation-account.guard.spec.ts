import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { lastValueFrom, of } from 'rxjs';

import { ActivatedRouteSnapshotStub, ActivatedRouteStub, mockClass } from '@testing';

import {
  AviationAccountReportingStatusService,
  AviationAccountsService,
  AviationAccountUpdateService,
  AviationAccountViewService,
} from 'pmrv-api';

import { AviationAccountsStore } from '../store';
import { mockedAccount, mockReportingStatusResults } from '../testing/mock-data';
import { AviationAccountGuard } from './aviation-account.guard';

let guard: AviationAccountGuard;

describe('AviationAccountGuard', () => {
  let accountsService;
  let aviationAccountReportingStatusService;
  let store: AviationAccountsStore;
  const activatedRoute = new ActivatedRouteStub({ accountId: '1' });

  const mockResults = {
    reportingStatusList: mockReportingStatusResults,
    total: 5,
  } as any;

  beforeEach(() => {
    accountsService = mockClass(AviationAccountViewService);
    aviationAccountReportingStatusService = mockClass(AviationAccountReportingStatusService);

    accountsService.getAviationAccountById.mockReturnValueOnce(of(mockedAccount));

    aviationAccountReportingStatusService.getAllReportingStatuses.mockReturnValueOnce(of(mockResults));

    TestBed.configureTestingModule({
      providers: [
        AviationAccountsStore,
        provideHttpClient(),
        AviationAccountGuard,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: AviationAccountsService, useValue: mockClass(AviationAccountsService) },
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
        { provide: AviationAccountViewService, useValue: accountsService },
        { provide: AviationAccountReportingStatusService, useValue: aviationAccountReportingStatusService },
      ],
    });

    guard = TestBed.inject(AviationAccountGuard);
    store = TestBed.inject(AviationAccountsStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should check account existance', async () => {
    store.setReportingStatuses(mockResults.reportingStatusList);
    store.setReportingStatusTotal(5);

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1' }))),
    ).resolves.toBeTruthy();
  });
});
