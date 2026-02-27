import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { startOfDay } from 'date-fns';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { finalAlrFormProvider } from './final-alr-form.provider';

@Component({
  selector: 'app-surrender-final-alr',
  imports: [SharedModule],
  templateUrl: './final-alr.component.html',
  providers: [finalAlrFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SurrenderFinalAlrComponent {
  today = startOfDay(new Date());

  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../allowances'], { relativeTo: this.route });
    } else {
      const { alrRequired, alrReportDate } = this.form.value as Pick<
        PermitSurrenderReviewDeterminationGrant,
        'alrRequired' | 'alrReportDate'
      >;

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postReviewDetermination(
              {
                ...state.reviewDetermination,
                alrRequired,
                alrReportDate: alrRequired ? alrReportDate : null,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../allowances'], { relativeTo: this.route }));
    }
  }
}
