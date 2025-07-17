import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { catchError, map, Observable, of, tap, throwError } from 'rxjs';

import { OperatorInvitedUserInfoDTO, OperatorUsersRegistrationService } from 'pmrv-api';

import { isBadRequest } from '../../error/business-errors';
import { UserRegistrationStore } from '../store/user-registration.store';

export type OperatorInvitationResultData = { accountName: string; roleCode: string };
@Injectable({ providedIn: 'root' })
export class ClaimOperatorGuard {
  private operatorInvitationResultData: OperatorInvitationResultData;

  constructor(
    private readonly router: Router,
    private readonly operatorUsersRegistrationService: OperatorUsersRegistrationService,
    private readonly store: UserRegistrationStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const token = route.queryParamMap.get('token');

    return token
      ? this.operatorUsersRegistrationService.acceptOperatorInvitation({ token }).pipe(
          tap(
            (res) =>
              (this.operatorInvitationResultData = {
                accountName: res.accountName,
                roleCode: res.roleCode,
              }),
          ),
          map((res) => {
            const pendingInvitationStatuses: OperatorInvitedUserInfoDTO['invitationStatus'][] = [
              'PENDING_TO_REGISTERED_SET_REGISTER_FORM_NO_PASSWORD',
              'PENDING_TO_REGISTERED_SET_REGISTER_FORM',
              'ALREADY_REGISTERED_SET_PASSWORD_ONLY',
            ];

            if (pendingInvitationStatuses.includes(res.invitationStatus)) {
              this.store.setState({
                ...this.store.getState(),
                email: res.email,
                token,
                isInvited: true,
                userRegistrationDTO: {
                  ...this.store.getState().userRegistrationDTO,
                  ...{ email: res.email, firstName: res.firstName, lastName: res.lastName },
                },
                invitationStatus: res.invitationStatus,
              });

              switch (res.invitationStatus) {
                case 'PENDING_TO_REGISTERED_SET_REGISTER_FORM':
                case 'PENDING_TO_REGISTERED_SET_REGISTER_FORM_NO_PASSWORD':
                  this.router.navigate(['/registration/user/contact-details']);
                  break;
                case 'ALREADY_REGISTERED_SET_PASSWORD_ONLY':
                  this.router.navigate(['/registration/user/choose-password']);
                  break;
              }
            } else if (res.invitationStatus === 'ACCEPTED') {
              return true;
            }

            return false;
          }),
          catchError((res: unknown) => {
            if (isBadRequest(res)) {
              this.router.navigate(['/registration/invitation/invalid-link'], {
                queryParams: { code: res.error.code },
              });

              return of(false);
            } else {
              return throwError(() => res);
            }
          }),
        )
      : this.store.getState().token
        ? of(true)
        : of(this.router.parseUrl('landing'));
  }

  resolve(): OperatorInvitationResultData {
    return this.operatorInvitationResultData;
  }
}
