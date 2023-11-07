import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve } from '@angular/router';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { AuthStore, selectUserId } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { ApplicationUserDTO, UsersService, VerifierUserDTO, VerifierUsersService } from 'pmrv-api';

import { viewNotFoundVerifierError } from '../errors/business-error';

@Injectable({ providedIn: 'root' })
export class DetailsGuard implements CanActivate, Resolve<ApplicationUserDTO | VerifierUserDTO> {
  private verifier: ApplicationUserDTO | VerifierUserDTO;

  constructor(
    private readonly verifierUsersService: VerifierUsersService,
    private readonly usersService: UsersService,
    private readonly authStore: AuthStore,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  isVerifierActive(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.authStore.pipe(
      selectUserId,
      first(),
      switchMap((userId) => {
        return route.paramMap.get('userId') === userId
          ? this.usersService.getCurrentUser()
          : this.verifierUsersService.getVerifierUserById(route.paramMap.get('userId'));
      }),
      tap((verifier) => (this.verifier = verifier)),
      map((verifier) => !!verifier),
    );
  }

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.isVerifierActive(route).pipe(
      catchBadRequest(ErrorCodes.AUTHORITY1006, () => this.businessErrorService.showError(viewNotFoundVerifierError)),
    );
  }

  resolve(): ApplicationUserDTO | VerifierUserDTO {
    return this.verifier;
  }
}
