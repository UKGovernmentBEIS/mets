import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { AuthStore, selectUserState } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { ApplicationUserDTO, RegulatorUserDTO, RegulatorUsersService, UsersService } from 'pmrv-api';

import { saveNotFoundRegulatorError } from '../errors/business-error';

@Injectable({ providedIn: 'root' })
export class DeleteResolver implements Resolve<ApplicationUserDTO | RegulatorUserDTO> {
  constructor(
    private readonly regulatorUsersService: RegulatorUsersService,
    private readonly usersService: UsersService,
    private readonly authStore: AuthStore,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ApplicationUserDTO | RegulatorUserDTO> {
    return this.authStore
      .pipe(
        selectUserState,
        first(),
        switchMap(({ userId }) =>
          userId === route.paramMap.get('userId')
            ? this.usersService.getCurrentUser()
            : this.regulatorUsersService.getRegulatorUserByCaAndId(route.paramMap.get('userId')),
        ),
      )
      .pipe(
        catchBadRequest(ErrorCodes.AUTHORITY1003, () =>
          this.businessErrorService.showError(saveNotFoundRegulatorError),
        ),
      );
  }
}
