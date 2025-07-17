import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { Observable } from 'rxjs';

import { OperatorUserDTO, UserDTO } from 'pmrv-api';

import { BusinessErrorService } from '../../../../../error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '../../../../../error/business-errors';
import { DetailsGuard } from '../details/details.guard';
import { saveNotFoundOperatorError } from '../errors/business-error';

@Injectable({ providedIn: 'root' })
export class DeleteGuard {
  constructor(
    private readonly operatorDetailsGuard: DetailsGuard,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.operatorDetailsGuard
      .isOperatorActive(route)
      .pipe(
        catchBadRequest(ErrorCodes.AUTHORITY1004, () =>
          this.businessErrorService.showError(saveNotFoundOperatorError(Number(route.paramMap.get('accountId')))),
        ),
      );
  }

  resolve(): OperatorUserDTO | UserDTO {
    return this.operatorDetailsGuard.resolve();
  }
}
