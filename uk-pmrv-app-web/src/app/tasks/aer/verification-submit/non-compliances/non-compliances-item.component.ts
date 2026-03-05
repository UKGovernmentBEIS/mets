import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { nonCompliancesItemFormProvider } from '@tasks/aer/verification-submit/non-compliances/non-compliances-item-form.provider';

import {
  AerApplicationVerificationSubmitRequestTaskPayload,
  UncorrectedItem,
  UncorrectedNonCompliances,
} from 'pmrv-api';

@Component({
  selector: 'app-non-compliances-item',
  templateUrl: './non-compliances-item.component.html',
  providers: [nonCompliancesItemFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonCompliancesItemComponent implements PendingRequest {
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
            const nonCompliances = (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
              .uncorrectedNonCompliances;
            const uncorrectedItems: UncorrectedItem[] =
              index >= (nonCompliances?.uncorrectedNonCompliances?.length ?? 0)
                ? [
                    ...(nonCompliances?.uncorrectedNonCompliances ?? []),
                    { ...this.form.value, reference: `C${index + 1}` },
                  ]
                : nonCompliances.uncorrectedNonCompliances.map((item, idx) =>
                    idx === index ? { ...item, ...this.form.value } : item,
                  );

            return this.aerService.postVerificationTaskSave(
              {
                uncorrectedNonCompliances: {
                  ...nonCompliances,
                  uncorrectedNonCompliances: uncorrectedItems,
                } as UncorrectedNonCompliances,
              },
              false,
              'uncorrectedNonCompliances',
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
