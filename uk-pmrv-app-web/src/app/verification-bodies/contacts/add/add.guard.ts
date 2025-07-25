import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, UrlTree } from '@angular/router';

import { map, Observable, of, switchMap } from 'rxjs';

import { HttpStatuses } from '@error/http-status';

import { VerificationBodiesService } from 'pmrv-api';

import { BusinessErrorService } from '../../../error/business-error/business-error.service';
import { catchElseRethrow } from '../../../error/business-errors';
import { disabledVerificationBodyError, viewNotFoundVerificationBodyError } from '../../errors/business-error';

@Injectable({ providedIn: 'root' })
export class AddGuard {
  constructor(
    private readonly verificationBodyService: VerificationBodiesService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const verificationBodyId = Number(route.paramMap.get('verificationBodyId'));

    return this.verificationBodyService.getVerificationBodyById(verificationBodyId).pipe(
      map((response) => response.status !== 'DISABLED'),
      catchElseRethrow(
        (res) => res.status === HttpStatuses.NotFound,
        () => this.businessErrorService.showError(viewNotFoundVerificationBodyError),
      ),
      switchMap((isNotDisabled) =>
        isNotDisabled ? of(true) : this.businessErrorService.showError(disabledVerificationBodyError),
      ),
    );
  }
}
