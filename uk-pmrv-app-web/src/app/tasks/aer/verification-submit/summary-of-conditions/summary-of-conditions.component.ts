import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, mergeMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { summaryOfConditionsFormProvider } from '@tasks/aer/verification-submit/summary-of-conditions/summary-of-conditions-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload, SummaryOfConditions } from 'pmrv-api';

@Component({
  selector: 'app-summary-of-conditions',
  templateUrl: './summary-of-conditions.component.html',
  providers: [summaryOfConditionsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryOfConditionsComponent {
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
      this.aerService
        .getPayload()
        .pipe(
          first(),
          map(
            (payload) =>
              (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.summaryOfConditions,
          ),
        )
        .subscribe((summaryOfConditionsInfo) => this.navigateNext(summaryOfConditionsInfo));
    } else {
      this.aerService
        .getPayload()
        .pipe(
          first(),
          map(
            (payload) =>
              (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.summaryOfConditions,
          ),
          mergeMap((summaryOfConditionsInfo) =>
            this.aerService
              .postVerificationTaskSave(
                {
                  summaryOfConditions: {
                    ...summaryOfConditionsInfo,
                    ...this.form.value,
                    ...(this.form.get('changesNotIncludedInPermit').value ? {} : { approvedChangesNotIncluded: null }),
                  },
                },
                false,
                'summaryOfConditions',
              )
              .pipe(map(() => summaryOfConditionsInfo)),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe((summaryOfConditionsInfo) => this.navigateNext(summaryOfConditionsInfo));
    }
  }

  private navigateNext(summaryOfConditions: SummaryOfConditions) {
    if (this.form.get('changesNotIncludedInPermit').value) {
      return summaryOfConditions?.approvedChangesNotIncluded?.length > 0
        ? this.router.navigate(['not-included-list'], { relativeTo: this.route })
        : this.router.navigate(['not-included-list', 0], { relativeTo: this.route });
    } else {
      return summaryOfConditions?.changesIdentified !== undefined
        ? this.router.navigate(['summary'], { relativeTo: this.route })
        : this.router.navigate(['identified-changes'], { relativeTo: this.route });
    }
  }
}
