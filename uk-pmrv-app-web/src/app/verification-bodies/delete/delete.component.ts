import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { HttpStatuses } from '@error/http-status';

import { VerificationBodiesService } from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchElseRethrow } from '../../error/business-errors';
import { saveNotFoundVerificationBodyError } from '../errors/business-error';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  isConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly verificationBodiesService: VerificationBodiesService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  delete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => Number(paramMap.get('verificationBodyId'))),
        switchMap((verificationBodyId) =>
          this.verificationBodiesService.deleteVerificationBodyById(verificationBodyId),
        ),
        catchElseRethrow(
          (res) => res.status === HttpStatuses.NotFound,
          () => this.businessErrorService.showError(saveNotFoundVerificationBodyError),
        ),
      )
      .subscribe(() => this.isConfirmationDisplayed$.next(true));
  }
}
