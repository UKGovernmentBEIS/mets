import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { complianceMonitoringFormProvider } from '@tasks/aer/verification-submit/compliance-monitoring/compliance-monitoring-form.provider';

@Component({
  selector: 'app-compliance-monitoring',
  templateUrl: './compliance-monitoring.component.html',
  providers: [complianceMonitoringFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComplianceMonitoringComponent {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['summary'], { relativeTo: this.route });
    } else {
      this.aerService
        .postVerificationTaskSave(
          {
            complianceMonitoringReporting: {
              ...this.form.value,
            },
          },
          false,
          'complianceMonitoringReporting',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
    }
  }
}
