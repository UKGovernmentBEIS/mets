import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, pluck, switchMap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';

import { PermitSurrenderReviewDeterminationDeemWithdraw } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

@Component({
  selector: 'app-answers',
  template: `<app-page-heading>Check your answers</app-page-heading>
    <app-deem-withdraw-determination-summary-details
      [deemWithdrawDetermination$]="deemWithdrawDetermination$"
    ></app-deem-withdraw-determination-summary-details>
    <div class="govuk-button-group">
      <button appPendingButton govukButton type="button" (click)="confirm()" *ngIf="isEditable$ | async">
        Confirm and complete
      </button>
    </div>
    <a govukLink routerLink="../../..">Return to: Permit surrender review</a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AnswersComponent implements PendingRequest {
  deemWithdrawDetermination$ = this.store.pipe(
    pluck('reviewDetermination'),
  ) as Observable<PermitSurrenderReviewDeterminationDeemWithdraw>;
  isEditable$ = this.store.pipe(pluck('isEditable'));

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
