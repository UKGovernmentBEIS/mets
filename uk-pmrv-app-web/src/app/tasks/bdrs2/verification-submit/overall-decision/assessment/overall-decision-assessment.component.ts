import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';

import { overallAssessmentFormProvider } from './overall-decision-assessment-form.provider';

@Component({
  selector: 'app-bdrs2-overall-decision-assessment',
  imports: [BdrS2TaskSharedModule, SharedModule],
  standalone: true,
  templateUrl: './overall-decision-assessment.component.html',
  providers: [overallAssessmentFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionAssessmentComponent {
  isEditable = this.bdrs2Service.isEditable;
  payload = this.bdrs2Service.payload;

  constructor(
    @Inject(BDRS2_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
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
      this.bdrs2Service
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
