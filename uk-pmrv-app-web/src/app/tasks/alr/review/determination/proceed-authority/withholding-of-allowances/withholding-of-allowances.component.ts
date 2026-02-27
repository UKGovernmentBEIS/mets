import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { alrWithholdingOfAllowancesFormProvider } from './withholding-of-allowances-form.provider';

@Component({
  selector: 'app-alr-withholding-of-allowances',
  imports: [SharedModule, AlrTaskSharedModule],
  templateUrl: './withholding-of-allowances.component.html',
  providers: [alrWithholdingOfAllowancesFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrWithholdingOfAllowancesComponent {
  isEditable = this.alrService.isEditable;

  private readonly payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    const nextStep = ['../', 'preliminary-allocation'];

    if (!this.form.dirty) {
      this.router.navigate(nextStep, { relativeTo: this.route });
    } else {
      const payload = this.payload();
      const formValues = this.form.value;

      this.alrService
        .postAlrReview(
          {
            ...payload?.regulatorReviewOutcome,
            determination: {
              ...payload.regulatorReviewOutcome.determination,
              hasWithholdingOfAllowances: formValues.hasWithholdingOfAllowances,
              withholdingAllowancesNotice: formValues.hasWithholdingOfAllowances
                ? {
                    noticeIssuedDate: formValues.noticeIssuedDate,
                    withholdingOfAllowancesComment: formValues.withholdingOfAllowancesComment,
                  }
                : undefined,
            },
          },
          'DETERMINATION',
          false,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(nextStep, { relativeTo: this.route }));
    }
  }
}
