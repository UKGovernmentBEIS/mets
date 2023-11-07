import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { lastValueFrom, of, throwError } from 'rxjs';

import { CaExternalContactsService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../testing';
import { BusinessTestingModule, expectBusinessErrorToBe } from '../../../error/testing/business-error';
import { saveNotFoundExternalContactError } from '../../errors/business-error';
import { DeleteGuard } from './delete.guard';

describe('DeleteGuard', () => {
  let guard: DeleteGuard;

  const response = { contact: { id: '1', name: 'Dexter', email: 'dexter@lab.com', description: 'A scientist' } };
  let caExternalContactsService: Partial<jest.Mocked<CaExternalContactsService>>;

  beforeEach(() => {
    caExternalContactsService = {
      getCaExternalContactById: jest.fn().mockReturnValue(of(response)),
    };

    TestBed.configureTestingModule({
      imports: [BusinessTestingModule],
      providers: [{ provide: CaExternalContactsService, useValue: caExternalContactsService }],
    });
    guard = TestBed.inject(DeleteGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access and resolve the contact if the contact is found', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '1' }))),
    ).resolves.toBeTruthy();

    expect(caExternalContactsService.getCaExternalContactById).toHaveBeenCalledWith(1);

    expect(guard.resolve()).toEqual(response);
  });

  it('should display business error page if the contact is not found', async () => {
    caExternalContactsService.getCaExternalContactById.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'EXTCONTACT1000' } })),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '1' }))),
    ).rejects.toBeTruthy();
    await expectBusinessErrorToBe(saveNotFoundExternalContactError);
  });
});
