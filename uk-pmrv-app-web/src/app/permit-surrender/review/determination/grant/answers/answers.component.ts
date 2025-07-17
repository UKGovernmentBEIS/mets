import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

@Component({
  selector: 'app-answers',
  template: `
    <app-page-heading>Check your answers</app-page-heading>
    <app-grant-determination-summary-details
      [grantDetermination$]="grantDetermination$"></app-grant-determination-summary-details>
    <div class="govuk-button-group">
      <button appPendingButton govukButton type="button" (click)="confirm()" *ngIf="isEditable$ | async">
        Confirm and complete
      </button>
    </div>
    <a govukLink routerLink="../../..">Return to: Surrender permit determination</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AnswersComponent implements PendingRequest {
  grantDetermination$ = this.store.pipe(
    map((state) => state?.reviewDetermination),
  ) as Observable<PermitSurrenderReviewDeterminationGrant>;
  isEditable$ = this.store.pipe(map((state) => state?.isEditable));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitSurrenderStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  confirm(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) => this.store.postReviewDetermination(state.reviewDetermination, true)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
