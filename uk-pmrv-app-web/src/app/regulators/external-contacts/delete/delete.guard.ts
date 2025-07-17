import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { Observable } from 'rxjs';

import { CaExternalContactDTO } from 'pmrv-api';

import { BusinessErrorService } from '../../../error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '../../../error/business-errors';
import { saveNotFoundExternalContactError } from '../../errors/business-error';
import { DetailsGuard } from '../details/details.guard';

@Injectable({ providedIn: 'root' })
export class DeleteGuard {
  constructor(
    private readonly externalContactDetailsGuard: DetailsGuard,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.externalContactDetailsGuard
      .isExternalContactActive(route)
      .pipe(
        catchBadRequest(ErrorCodes.EXTCONTACT1000, () =>
          this.businessErrorService.showError(saveNotFoundExternalContactError),
        ),
      );
  }

  resolve(): CaExternalContactDTO {
    return this.externalContactDetailsGuard.resolve();
  }
}
