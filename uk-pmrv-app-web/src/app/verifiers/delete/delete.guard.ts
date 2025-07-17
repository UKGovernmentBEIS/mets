import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { Observable } from 'rxjs';

import { UserDTO, VerifierUserDTO } from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '../../error/business-errors';
import { DetailsGuard } from '../details/details.guard';
import { saveNotFoundVerifierError } from '../errors/business-error';

@Injectable({ providedIn: 'root' })
export class DeleteGuard {
  constructor(
    private readonly verifierDetailsGuard: DetailsGuard,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.verifierDetailsGuard
      .isVerifierActive(route)
      .pipe(
        catchBadRequest(ErrorCodes.AUTHORITY1006, () => this.businessErrorService.showError(saveNotFoundVerifierError)),
      );
  }

  resolve(): UserDTO | VerifierUserDTO {
    return this.verifierDetailsGuard.resolve();
  }
}
