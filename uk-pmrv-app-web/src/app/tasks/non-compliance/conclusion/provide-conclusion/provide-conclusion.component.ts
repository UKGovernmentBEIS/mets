import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { NonComplianceService } from '@tasks/non-compliance/core/non-compliance.service';
import { NON_COMPLIANCE_TASK_FORM } from '@tasks/non-compliance/core/non-compliance-form.token';

import { provideConclusionFormProvider } from './provide-conclusion-form.provider';

@Component({
  selector: 'app-provide-conclusion',
  templateUrl: './provide-conclusion.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideConclusionFormProvider],
})
export class ProvideConclusionComponent {
  businessErrorService: any;
  nextRoute = '../summary';
  today = new Date();
  constructor(
    @Inject(NON_COMPLIANCE_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    readonly nonComplianceService: NonComplianceService,
  ) {}

  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';

  onSubmit() {
    if (!this.form.dirty) {
      this.router.navigate([this.nextRoute], { relativeTo: this.route }).then();
    } else {
      return this.nonComplianceService
        .saveConclusion(
          {
            complianceRestored: this.form.controls.complianceRestored?.value,
            complianceRestoredDate: this.form.controls.complianceRestored?.value
              ? this.form.controls.complianceRestoredDate?.value
              : null,
            comments: this.form.controls.comments?.value,
            reissuePenalty: this.form.controls.reissuePenalty?.value,
            operatorPaid: this.form.controls.operatorPaid?.value,
            operatorPaidDate: this.form.controls.operatorPaid?.value
              ? this.form.controls.operatorPaidDate?.value
              : null,
          },
          false,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([this.nextRoute], { relativeTo: this.route }));
    }
  }
}
