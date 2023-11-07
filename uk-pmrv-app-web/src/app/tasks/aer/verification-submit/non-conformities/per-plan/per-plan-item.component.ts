import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { perPlanItemFormProvider } from '@tasks/aer/verification-submit/non-conformities/per-plan/per-plan-item-form.provider';

import {
  AerApplicationVerificationSubmitRequestTaskPayload,
  UncorrectedItem,
  UncorrectedNonConformities,
} from 'pmrv-api';

@Component({
  selector: 'app-per-plan-item',
  templateUrl: './per-plan-item.component.html',
  providers: [perPlanItemFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerPlanItemComponent implements PendingRequest {
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
            const uncorrectedItems: UncorrectedItem[] =
              index >= (nonConformities?.uncorrectedNonConformities?.length ?? 0)
                ? [
                    ...(nonConformities?.uncorrectedNonConformities ?? []),
                    { ...this.form.value, reference: `B${index + 1}` },
                  ]
                : nonConformities.uncorrectedNonConformities.map((item, idx) =>
                    idx === index ? { ...item, ...this.form.value } : item,
                  );

            return this.aerService.postVerificationTaskSave(
              {
                uncorrectedNonConformities: {
                  ...nonConformities,
                  uncorrectedNonConformities: uncorrectedItems,
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
