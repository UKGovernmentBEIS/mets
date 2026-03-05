import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { overallDecisionFormProvider } from '@tasks/aer/verification-submit/overall-decision/overall-decision-form.provider';

import { OverallAssessment } from 'pmrv-api';

@Component({
  selector: 'app-overall-decision',
  templateUrl: './overall-decision.component.html',
  providers: [overallDecisionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionComponent {
  isEditable$ = this.aerService.isEditable$;
  overallAssessmentTypes: OverallAssessment['type'][] = [
    'VERIFIED_AS_SATISFACTORY',
    'VERIFIED_WITH_COMMENTS',
    'NOT_VERIFIED',
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
              ...this.form.value,
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
    switch (this.form.get('type').value) {
      case 'VERIFIED_AS_SATISFACTORY':
        return this.router.navigate(['summary'], { relativeTo: this.route });
      case 'VERIFIED_WITH_COMMENTS':
        return this.router.navigate(['reason-list'], { relativeTo: this.route });
      case 'NOT_VERIFIED':
        return this.router.navigate(['not-verified'], { relativeTo: this.route });
    }
  }
}
