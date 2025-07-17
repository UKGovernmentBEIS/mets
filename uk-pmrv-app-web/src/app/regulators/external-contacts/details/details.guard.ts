import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { CaExternalContactDTO, CaExternalContactsService } from 'pmrv-api';

import { viewNotFoundExternalContactError } from '../../errors/business-error';

@Injectable({ providedIn: 'root' })
export class DetailsGuard {
  private externalContact: CaExternalContactDTO;

  constructor(
    private readonly caExternalContactsService: CaExternalContactsService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  isExternalContactActive(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.caExternalContactsService.getCaExternalContactById(Number(route.paramMap.get('userId'))).pipe(
      tap((contact) => (this.externalContact = contact)),
      map(() => true),
    );
  }

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.isExternalContactActive(route).pipe(
      catchBadRequest(ErrorCodes.EXTCONTACT1000, () =>
        this.businessErrorService.showError(viewNotFoundExternalContactError),
      ),
    );
  }

  resolve(): CaExternalContactDTO {
    return this.externalContact;
  }
}
