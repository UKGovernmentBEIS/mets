import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { AuthStore, selectUserId } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { RegulatorCurrentUserDTO, RegulatorUserDTO, RegulatorUsersService, UsersService } from 'pmrv-api';

import { viewNotFoundRegulatorError } from '../errors/business-error';

@Injectable({ providedIn: 'root' })
export class DetailsResolver {
  constructor(
    private readonly regulatorUsersService: RegulatorUsersService,
    private readonly usersService: UsersService,
    private readonly authStore: AuthStore,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<RegulatorUserDTO | RegulatorCurrentUserDTO> {
    return this.authStore.pipe(
      selectUserId,
      first(),
      switchMap((userId) =>
        userId === route.paramMap.get('userId')
          ? this.usersService.getCurrentUser()
          : this.regulatorUsersService.getRegulatorUserByCaAndId(route.paramMap.get('userId')),
      ),
      catchBadRequest(ErrorCodes.AUTHORITY1003, () => this.businessErrorService.showError(viewNotFoundRegulatorError)),
    );
  }
}
