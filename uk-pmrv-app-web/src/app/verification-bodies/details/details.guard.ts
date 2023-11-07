import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Resolve } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { HttpStatuses } from '@error/http-status';

import { VerificationBodiesService, VerificationBodyDTO } from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchElseRethrow } from '../../error/business-errors';
import { viewNotFoundVerificationBodyError } from '../errors/business-error';

@Injectable({ providedIn: 'root' })
export class DetailsGuard implements CanActivate, Resolve<VerificationBodyDTO> {
  private body: VerificationBodyDTO;

  constructor(
    private readonly verificationBodiesService: VerificationBodiesService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.verificationBodiesService
      .getVerificationBodyById(Number(route.paramMap.get('verificationBodyId')))
      .pipe(
        tap((response) => (this.body = response)),
        map((response) => !!response),
        catchElseRethrow(
          (res) => res.status === HttpStatuses.NotFound,
          () => this.businessErrorService.showError(viewNotFoundVerificationBodyError),
        ),
      );
  }

  resolve(): VerificationBodyDTO {
    return this.body;
  }
}
