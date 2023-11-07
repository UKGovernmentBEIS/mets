import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { identifiedChangesItemFormProvider } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/identified-changes-item-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-identified-changes-item',
  templateUrl: './identified-changes-item.component.html',
  providers: [identifiedChangesItemFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IdentifiedChangesItemComponent {
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
      this.router.navigate(['../../identified-changes-list'], { relativeTo: this.route });
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
                  notReportedChanges:
                    index >= (summaryOfConditionsInfo?.notReportedChanges?.length ?? 0)
                      ? [
                          ...(summaryOfConditionsInfo?.notReportedChanges ?? []),
                          { ...this.form.value, reference: `B${index + 1}` },
                        ]
                      : summaryOfConditionsInfo.notReportedChanges.map((item, idx) =>
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
        .subscribe(() => this.router.navigate(['../../identified-changes-list'], { relativeTo: this.route }));
    }
  }
}
