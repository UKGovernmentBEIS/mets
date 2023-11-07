import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { WITHHOLDING_ALLOWANCES_TASK_FORM } from '@tasks/withholding-allowances/core/withholding-allowances';
import { WithholdingAllowancesService } from '@tasks/withholding-allowances/core/withholding-allowances.service';

import { reasonFormProvider } from './withdraw-reason-form.provider';

@Component({
  selector: 'app-withdraw-reason',
  templateUrl: './withdraw-reason.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [PendingRequestService, reasonFormProvider],
})
export class WithdrawReasonComponent {
  constructor(
    @Inject(WITHHOLDING_ALLOWANCES_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    private readonly withholdingAllowancesService: WithholdingAllowancesService,
  ) {}

  onSubmit() {
    const nextRoute = `../summary`;

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      this.withholdingAllowancesService
        .postTaskSave(
          {
            withholdingWithdrawal: {
              reason: this.form.get('reason').value,
            },
          },
          false,
          'WITHDRAWAL_REASON_CHANGE',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route, state: { notification: true } }));
    }
  }
}
