import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { notVerifiedFormProvider } from '@tasks/aer/verification-submit/overall-decision/not-verified/not-verified-form.provider';

import { NotVerifiedReason } from 'pmrv-api';

@Component({
  selector: 'app-not-verified',
  templateUrl: './not-verified.component.html',
  providers: [notVerifiedFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotVerifiedComponent {
  isEditable$ = this.aerService.isEditable$;
  notOverallVerificationReasonTypes: NotVerifiedReason['type'][] = [
    'UNCORRECTED_MATERIAL_MISSTATEMENT',
    'UNCORRECTED_MATERIAL_NON_CONFORMITY',
    'DATA_OR_INFORMATION_LIMITATIONS',
    'SCOPE_LIMITATIONS_CLARITY',
    'NOT_APPROVED_MONITORING_PLAN',
    'SCOPE_LIMITATIONS_MONITORING_PLAN',
  ];

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      this.aerService
        .postVerificationTaskSave(
          {
            overallAssessment: {
              type: 'NOT_VERIFIED',
              notVerifiedReasons: this.form.get('type').value.map(
                (reason) =>
                  ({
                    type: reason,
                    ...(reason === 'ANOTHER_REASON' ? { otherReason: this.form.get('otherReason').value } : undefined),
                  }) as NotVerifiedReason,
              ),
            },
          },
          false,
          'overallAssessment',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.nextUrl());
    }
  }

  private nextUrl() {
    return this.router.navigate(['../summary'], { relativeTo: this.route });
  }
}
