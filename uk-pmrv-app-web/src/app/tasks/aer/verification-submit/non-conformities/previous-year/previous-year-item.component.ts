import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { previousYearItemFormProvider } from '@tasks/aer/verification-submit/non-conformities/previous-year/previous-year-item-form.provider';

import {
  AerApplicationVerificationSubmitRequestTaskPayload,
  UncorrectedNonConformities,
  VerifierComment,
} from 'pmrv-api';

@Component({
  selector: 'app-previous-year-item',
  templateUrl: './previous-year-item.component.html',
  providers: [previousYearItemFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreviousYearItemComponent implements PendingRequest {
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
      this.nextUrl().then();
    } else {
      combineLatest([this.aerService.getPayload(), this.route.paramMap])
        .pipe(
          first(),
          switchMap(([payload, paramMap]) => {
            const index = Number(paramMap.get('index'));
            const nonConformities = (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
              .uncorrectedNonConformities;
            const priorYearIssues: VerifierComment[] =
              index >= (nonConformities?.priorYearIssues?.length ?? 0)
                ? [...(nonConformities?.priorYearIssues ?? []), { ...this.form.value, reference: `E${index + 1}` }]
                : nonConformities.priorYearIssues.map((item, idx) =>
                    idx === index ? { ...item, ...this.form.value } : item,
                  );

            return this.aerService.postVerificationTaskSave(
              {
                uncorrectedNonConformities: {
                  ...nonConformities,
                  priorYearIssues: priorYearIssues,
                } as UncorrectedNonConformities,
              },
              false,
              'uncorrectedNonConformities',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.nextUrl());
    }
  }

  private nextUrl() {
    return this.router.navigate(['../list'], { relativeTo: this.route });
  }
}
