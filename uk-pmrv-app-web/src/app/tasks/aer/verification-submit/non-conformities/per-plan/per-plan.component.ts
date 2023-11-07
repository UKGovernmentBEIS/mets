import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, iif, map, mergeMap, of } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { perPlanFormProvider } from '@tasks/aer/verification-submit/non-conformities/per-plan/per-plan-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload, UncorrectedNonConformities } from 'pmrv-api';

@Component({
  selector: 'app-per-plan',
  templateUrl: './per-plan.component.html',
  providers: [perPlanFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerPlanComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.aerService
      .getPayload()
      .pipe(
        first(),
        map(
          (payload) =>
            (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
              .uncorrectedNonConformities,
        ),
        mergeMap((uncorrectedNonConformities) => {
          return iif(
            () => this.form.dirty,
            this.aerService
              .postVerificationTaskSave(
                {
                  uncorrectedNonConformities: {
                    ...uncorrectedNonConformities,
                    ...this.form.value,
                    ...(!this.form.get('areThereUncorrectedNonConformities').value
                      ? { uncorrectedNonConformities: null }
                      : {}),
                  },
                },
                false,
                'uncorrectedNonConformities',
              )
              .pipe(map(() => uncorrectedNonConformities)),
            of(uncorrectedNonConformities),
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((uncorrectedNonConformities) => this.nextUrl(uncorrectedNonConformities));
  }

  private nextUrl(uncorrectedNonConformities: UncorrectedNonConformities) {
    return this.form.get('areThereUncorrectedNonConformities').value
      ? uncorrectedNonConformities?.uncorrectedNonConformities?.length > 0
        ? this.router.navigate(['per-plan', 'list'], { relativeTo: this.route })
        : this.router.navigate(['per-plan', 0], { relativeTo: this.route })
      : this.router.navigate(['previous-year'], { relativeTo: this.route });
  }
}
