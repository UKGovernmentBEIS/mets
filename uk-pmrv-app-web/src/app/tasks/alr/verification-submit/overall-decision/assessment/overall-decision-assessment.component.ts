import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { overallAssessmentFormProvider } from './overall-decision-assessment-form.provider';

@Component({
  selector: 'app-alr-overall-decision-assessment',
  standalone: true,
  imports: [AlrTaskSharedModule, SharedModule],
  providers: [overallAssessmentFormProvider],
  templateUrl: './overall-decision-assessment.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionAssessmentComponent {
  isEditable = this.alrService.isEditable;
  payload = this.alrService.payload;

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
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
      this.alrService
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
