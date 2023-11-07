import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { InstallationAccountViewService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass, MockType } from '../../testing';
import { AccountGuard } from './account.guard';
import { mockedAccountPermit } from './testing/mock-data';

describe('AccountGuard', () => {
  let guard: AccountGuard;
  let accountViewService: MockType<InstallationAccountViewService>;

  beforeEach(() => {
    accountViewService = mockClass(InstallationAccountViewService);
    accountViewService.getInstallationAccountById.mockReturnValueOnce(of(mockedAccountPermit));

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [AccountGuard, { provide: InstallationAccountViewService, useValue: accountViewService }],
    });
    guard = TestBed.inject(AccountGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return account details', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1' }))),
    ).resolves.toBeTruthy();

    expect(accountViewService.getInstallationAccountById).toHaveBeenCalledWith(1);

    await expect(guard.resolve()).toEqual(mockedAccountPermit);
  });
});
