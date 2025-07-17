import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of, throwError } from 'rxjs';

import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { InvitedUserInfoDTO, VerifierUsersRegistrationService } from 'pmrv-api';

import { VerifierInvitationGuard } from './verifier-invitation.guard';

describe('VerifierInvitationGuard', () => {
  let guard: VerifierInvitationGuard;
  let router: Router;
  let verifierUsersRegistrationService: jest.Mocked<VerifierUsersRegistrationService>;

  beforeEach(() => {
    verifierUsersRegistrationService = mockClass(VerifierUsersRegistrationService);
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: VerifierUsersRegistrationService, useValue: verifierUsersRegistrationService }],
    });
    guard = TestBed.inject(VerifierInvitationGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should navigate to root if there is no token query param', async () => {
    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub()))).resolves.toEqual(
      router.parseUrl('landing'),
    );
    expect(verifierUsersRegistrationService.acceptVerifierInvitation).not.toHaveBeenCalled();
  });

  it('should navigate to invalid link for all 400 errors', async () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    verifierUsersRegistrationService.acceptVerifierInvitation.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'testCode' }, status: 400 })),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub(null, { token: 'email-token' }))),
    ).resolves.toBeFalsy();
    expect(navigateSpy).toHaveBeenCalledWith(['invitation/verifier/invalid-link'], {
      queryParams: { code: 'testCode' },
    });
  });

  it('should resolved the invited user', async () => {
    const invitedUser: InvitedUserInfoDTO = {
      email: 'user@pmrv.uk',
      invitationStatus: 'ALREADY_REGISTERED_SET_PASSWORD_ONLY',
    };
    verifierUsersRegistrationService.acceptVerifierInvitation.mockReturnValue(of(invitedUser));

    await lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub(undefined, { token: 'token' })));

    expect(guard.resolve()).toEqual(invitedUser);
    expect(verifierUsersRegistrationService.acceptVerifierInvitation).toHaveBeenCalledWith({
      token: 'token',
    });
  });

  it('should resolved the invited user and navigate to confirmed when invitation status is already registered', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const invitedUser: InvitedUserInfoDTO = { email: 'user@pmrv.uk', invitationStatus: 'ALREADY_REGISTERED' };
    verifierUsersRegistrationService.acceptVerifierInvitation.mockReturnValue(of(invitedUser));

    await lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub(undefined, { token: 'token' })));
    expect(guard.resolve()).toEqual(invitedUser);
    expect(verifierUsersRegistrationService.acceptVerifierInvitation).toHaveBeenCalledWith({
      token: 'token',
    });
    expect(navigateSpy).toHaveBeenCalledWith(['invitation/verifier/confirmed']);
  });
});
