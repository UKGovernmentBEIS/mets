import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { notIncludedItemFormProvider } from '@tasks/aer/verification-submit/summary-of-conditions/not-included-list/not-included-item-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-not-included-item',
  templateUrl: './not-included-item.component.html',
  providers: [notIncludedItemFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotIncludedItemComponent {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../../not-included-list'], { relativeTo: this.route });
    } else {
      combineLatest([this.aerService.getPayload(), this.route.paramMap])
        .pipe(
          first(),
          switchMap(([payload, paramMap]) => {
            const index = Number(paramMap.get('index'));
            const summaryOfConditionsInfo = (payload as AerApplicationVerificationSubmitRequestTaskPayload)
              .verificationReport.summaryOfConditions;

            return this.aerService.postVerificationTaskSave(
              {
                summaryOfConditions: {
                  ...summaryOfConditionsInfo,
                  approvedChangesNotIncluded:
                    index >= (summaryOfConditionsInfo?.approvedChangesNotIncluded?.length ?? 0)
                      ? [
                          ...(summaryOfConditionsInfo?.approvedChangesNotIncluded ?? []),
                          { ...this.form.value, reference: `A${index + 1}` },
                        ]
                      : summaryOfConditionsInfo.approvedChangesNotIncluded.map((item, idx) =>
                          idx === index ? { ...item, ...this.form.value } : item,
                        ),
                },
              },
              false,
              'summaryOfConditions',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../../not-included-list'], { relativeTo: this.route }));
    }
  }
}
