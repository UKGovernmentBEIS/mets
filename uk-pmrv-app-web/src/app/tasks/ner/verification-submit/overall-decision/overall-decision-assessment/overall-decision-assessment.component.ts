import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';
import { NER_TASK_FORM, NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared';

import { nerOverallAssessmentFormProvider } from './overall-decision-assessment-form.provider';

@Component({
  selector: 'app-ner-overall-decision-assessment',
  imports: [NerTaskComponent, SharedModule],
  templateUrl: './overall-decision-assessment.component.html',
  providers: [nerOverallAssessmentFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerOverallDecisionAssessmentComponent {
  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pendingRequest = inject(PendingRequestService);

  isEditable = this.nerService.isEditable;
  requestTaskType = this.nerService.requestTaskType;
  form = inject<UntypedFormGroup>(NER_TASK_FORM);

  onSubmit() {
    const formValues = this.form.value;

    let reasons: OverallVerificationAssessment['reasons'];

    if (formValues.type === 'VERIFIED_WITH_COMMENTS') {
      reasons = formValues.reasons;
    } else if (formValues.type === 'NOT_VERIFIED') {
      reasons = formValues.reasonsNotVerified;
    } else {
      reasons = undefined;
    }

    this.nerService
      .postVerificationTaskSave(
        {
          overallAssessment: {
            type: formValues.type,
            reasons,
          } as OverallVerificationAssessment,
        },
        false,
        'OVERALL_DECISION',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
  }
}
