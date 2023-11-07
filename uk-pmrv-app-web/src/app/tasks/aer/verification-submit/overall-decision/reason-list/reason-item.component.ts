import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { reasonItemFormProvider } from '@tasks/aer/verification-submit/overall-decision/reason-list/reason-item-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload, VerifiedWithCommentsOverallAssessment } from 'pmrv-api';

@Component({
  selector: 'app-reason-item',
  templateUrl: './reason-item.component.html',
  providers: [reasonItemFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReasonItemComponent {
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
      this.nextUrl();
    } else {
      combineLatest([this.aerService.getPayload(), this.route.paramMap])
        .pipe(
          first(),
          switchMap(([payload, paramMap]) => {
            const index = Number(paramMap.get('index'));
            const overallAssessmentInfo = (payload as AerApplicationVerificationSubmitRequestTaskPayload)
              .verificationReport.overallAssessment as VerifiedWithCommentsOverallAssessment;

            return this.aerService.postVerificationTaskSave(
              {
                overallAssessment: {
                  ...overallAssessmentInfo,
                  reasons:
                    index >= (overallAssessmentInfo?.reasons?.length ?? 0)
                      ? [...(overallAssessmentInfo?.reasons ?? []), this.form.get('reason').value]
                      : overallAssessmentInfo.reasons.map((item, idx) =>
                          idx === index ? this.form.get('reason').value : item,
                        ),
                },
              },
              false,
              'overallAssessment',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.nextUrl());
    }
  }

  private nextUrl() {
    return this.router.navigate(['..'], { relativeTo: this.route });
  }
}
