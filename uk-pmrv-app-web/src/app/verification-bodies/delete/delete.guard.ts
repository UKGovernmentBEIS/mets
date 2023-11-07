import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { HttpStatuses } from '@error/http-status';

import { VerificationBodiesService, VerificationBodyDTO } from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchElseRethrow } from '../../error/business-errors';
import { saveNotFoundVerificationBodyError } from '../errors/business-error';

@Injectable({ providedIn: 'root' })
export class DeleteGuard implements CanActivate {
  private body: VerificationBodyDTO;

  constructor(
    private readonly verificationBodiesService: VerificationBodiesService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.verificationBodiesService
      .getVerificationBodyById(Number(route.paramMap.get('verificationBodyId')))
      .pipe(
        tap((body) => (this.body = body)),
        map((body) => !!body),
        catchElseRethrow(
          (res) => res.status === HttpStatuses.NotFound,
          () => this.businessErrorService.showError(saveNotFoundVerificationBodyError),
        ),
      );
  }
}
