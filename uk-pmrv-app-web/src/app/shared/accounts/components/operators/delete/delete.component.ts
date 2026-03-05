import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, Observable, switchMap, withLatestFrom } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, selectCurrentDomain, selectLoginStatus, selectUserState } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { OperatorAuthoritiesService, OperatorUserDTO, UserDTO } from 'pmrv-api';

import {
  activeOperatorAdminError,
  financialContactError,
  primaryContactError,
  saveNotFoundOperatorError,
  serviceContactError,
} from '../errors/business-error';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  user$ = (this.route.data as Observable<{ user: OperatorUserDTO | UserDTO }>).pipe(map((data) => data.user));
  deleteStatus = new BehaviorSubject<'success' | null>(null);
  accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  isCurrentUser$ = combineLatest([this.route.paramMap, this.authStore.pipe(selectUserState)]).pipe(
    map(([paramMap, userState]) => paramMap.get('userId') === userState.userId),
  );
  currentDomain$ = this.authStore.pipe(selectCurrentDomain);
  loginStatus$ = this.currentDomain$.pipe(switchMap((domain) => this.authStore.pipe(selectLoginStatus(domain))));
  domainUrlPrefix$ = this.currentDomain$.pipe(map((domain) => (domain === 'AVIATION' ? '/aviation' : '')));

  constructor(
    readonly authService: AuthService,
    private readonly authStore: AuthStore,
    private readonly operatorAuthoritiesService: OperatorAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  confirm(): void {
    this.accountId$
      .pipe(
        first(),
        withLatestFrom(this.isCurrentUser$, this.route.paramMap.pipe(map((paramMap) => paramMap.get('userId')))),
        switchMap(([accountId, isCurrentUser, userId]) =>
          isCurrentUser
            ? this.operatorAuthoritiesService
                .deleteCurrentUserAccountOperatorAuthority(accountId)
                .pipe(switchMap(() => this.authService.loadUserState()))
            : this.operatorAuthoritiesService.deleteAccountOperatorAuthority(accountId, userId),
        ),
        catchBadRequest(
          [
            ErrorCodes.AUTHORITY1001,
            ErrorCodes.AUTHORITY1004,
            ErrorCodes.ACCOUNT_CONTACT1001,
            ErrorCodes.ACCOUNT_CONTACT1002,
            ErrorCodes.ACCOUNT_CONTACT1003,
          ],
          (res) =>
            this.accountId$.pipe(
              first(),
              switchMap((accountId) =>
                this.businessErrorService.showError(
                  (() => {
                    switch (res.error.code) {
                      case ErrorCodes.AUTHORITY1001:
                        return activeOperatorAdminError(accountId);
                      case ErrorCodes.AUTHORITY1004:
                        return saveNotFoundOperatorError(accountId);
                      case ErrorCodes.ACCOUNT_CONTACT1001:
                        return primaryContactError(accountId);
                      case ErrorCodes.ACCOUNT_CONTACT1002:
                        return financialContactError(accountId);
                      case ErrorCodes.ACCOUNT_CONTACT1003:
                        return serviceContactError(accountId);
                    }
                  })(),
                ),
              ),
            ),
        ),
      )
      .subscribe(() => this.deleteStatus.next('success'));
  }
}
