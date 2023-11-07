import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { WithholdingOfAllowances } from 'pmrv-api';

import { waReasons, WITHHOLDING_ALLOWANCES_TASK_FORM, years } from '../../core/withholding-allowances';
import { WithholdingAllowancesService } from '../../core/withholding-allowances.service';
import { recommendationResponseFormProvider } from './recovery-details-form.provider';

@Component({
  selector: 'app-recovery-details',
  templateUrl: './recovery-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [PendingRequestService, recommendationResponseFormProvider],
})
export class RecoveryDetailsComponent {
  years = years;
  waReasons = waReasons;

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
            withholdingOfAllowances: {
              ...this.getFormData(),
            },
          },
          false,
          'DETAILS_CHANGE',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route, state: { notification: true } }));
    }
  }

  private getFormData(): WithholdingOfAllowances {
    const reasonType = this.form.get('reasonType').value;
    return {
      year: this.form.get('year').value,
      reasonType,
      otherReason: reasonType === 'OTHER' ? this.form.get('otherReason').value : null,
      regulatorComments: this.form.get('regulatorComments').value,
    };
  }
}
