import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { startOfDay } from 'date-fns';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { allowancesFormProvider } from './allowances-form.provider';

@Component({
  selector: 'app-allowances',
  standalone: false,
  templateUrl: './allowances.component.html',
  providers: [allowancesFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AllowancesComponent implements PendingRequest {
  today = startOfDay(new Date());
  isFinalAlrVisible = toSignal(this.store.isFinalAlrVisible$);

  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      const allowancesSurrenderRequired: boolean = this.form.value.allowancesSurrenderRequired;
      const isFinalAlrVisible = this.isFinalAlrVisible();

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postReviewDetermination(
              {
                ...state.reviewDetermination,
                alrRequired: isFinalAlrVisible
                  ? (state.reviewDetermination as PermitSurrenderReviewDeterminationGrant).alrRequired
                  : undefined,
                alrReportDate: isFinalAlrVisible
                  ? (state.reviewDetermination as PermitSurrenderReviewDeterminationGrant).alrReportDate
                  : undefined,
                allowancesSurrenderRequired,
                allowancesSurrenderDate: allowancesSurrenderRequired ? this.form.value.allowancesSurrenderDate : null,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
    }
  }
}
