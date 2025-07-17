import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { BdrService, BdrTaskSharedModule } from '@tasks/bdr/shared';

import { overallAssessmentFormProvider } from './overall-decision-assessment-form.provider';

@Component({
  selector: 'app-bdr-overall-decision-assessment',
  standalone: true,
  imports: [BdrTaskSharedModule, SharedModule],
  providers: [overallAssessmentFormProvider],
  templateUrl: './overall-decision-assessment.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionAssessmentComponent {
  isEditable = this.bdrService.isEditable;
  payload = this.bdrService.payload;

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    const nextRoute = 'summary';
    const formValues = this.form.value;

    let reasons: OverallVerificationAssessment['reasons'];

    if (formValues.type === 'VERIFIED_WITH_COMMENTS') {
      reasons = formValues.reasons;
    } else if (formValues.type === 'NOT_VERIFIED') {
      reasons = formValues.reasonsNotVerified;
    } else {
      reasons = undefined;
    }

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      this.bdrService
        .postVerificationTaskSave(
          {
            overallAssessment: {
              type: formValues.type,
              reasons,
            } as OverallVerificationAssessment,
          },
          false,
          'overallDecision',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
