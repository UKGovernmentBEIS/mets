import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { catchError, map, Observable, of, tap, throwError } from 'rxjs';

import { InvitedUserInfoDTO, VerifierUsersRegistrationService } from 'pmrv-api';

import { isBadRequest } from '../../error/business-errors';

@Injectable({ providedIn: 'root' })
export class VerifierInvitationGuard {
  private invitedUser: InvitedUserInfoDTO;

  constructor(
    private readonly router: Router,
    private readonly verifierUsersRegistrationService: VerifierUsersRegistrationService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const token = route.queryParamMap.get('token');

    return token
      ? this.verifierUsersRegistrationService.acceptVerifierInvitation({ token }).pipe(
          tap((invitedUser) => (this.invitedUser = invitedUser)),
          map(() => {
            if (this.invitedUser.invitationStatus == 'ALREADY_REGISTERED') {
              this.router.navigate(['invitation/verifier/confirmed']);
              return;
            }
            if (
              ['PENDING_TO_REGISTERED_SET_PASSWORD_ONLY', 'ALREADY_REGISTERED_SET_PASSWORD_ONLY'].includes(
                this.invitedUser.invitationStatus,
              )
            ) {
              return true;
            }
            return false;
          }),
          catchError((res: unknown) => {
            if (isBadRequest(res)) {
              this.router.navigate(['invitation/verifier/invalid-link'], {
                queryParams: { code: res.error.code },
              });

              return of(false);
            } else {
              return throwError(() => res);
            }
          }),
        )
      : of(this.router.parseUrl('landing'));
  }

  resolve(): InvitedUserInfoDTO {
    return this.invitedUser;
  }
}
