import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { catchError, map, Observable, of, tap, throwError } from 'rxjs';

import { isBadRequest } from '@error/business-errors';

import { OperatorUsersRegistrationService } from 'pmrv-api';

import { UserRegistrationStore } from '../store/user-registration.store';

@Injectable({ providedIn: 'root' })
export class ConfirmedEmailGuard {
  constructor(
    private store: UserRegistrationStore,
    private router: Router,
    private operatorUsersRegistrationService: OperatorUsersRegistrationService,
  ) {}

  canActivate(next: ActivatedRouteSnapshot): Observable<boolean> | boolean {
    const token = next.queryParamMap.get('token');

    if (token) {
      return this.operatorUsersRegistrationService.verifyUserRegistrationToken({ token }).pipe(
        tap((operatorUserTokenVerificationResult) =>
          this.store.setState({
            ...this.store.getState(),
            email: operatorUserTokenVerificationResult.email,
            emailVerificationStatus: operatorUserTokenVerificationResult.status,
            token,
            isInvited: false,
          }),
        ),
        map(() => true),
        catchError((res: unknown) => {
          if (isBadRequest(res)) {
            this.router.navigate(['/registration/invalid-link'], { queryParams: { code: res.error.code } });

            return of(false);
          } else {
            return throwError(() => res);
          }
        }),
      );
    } else if (this.store.getState().token) {
      return true;
    } else {
      this.router.navigateByUrl('/registration');

      return false;
    }
  }
}
