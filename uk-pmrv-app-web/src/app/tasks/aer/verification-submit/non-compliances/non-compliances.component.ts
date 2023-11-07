import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, iif, map, mergeMap, of } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { nonCompliancesFormProvider } from '@tasks/aer/verification-submit/non-compliances/non-compliances-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload, UncorrectedNonCompliances } from 'pmrv-api';

@Component({
  selector: 'app-non-compliances',
  templateUrl: './non-compliances.component.html',
  providers: [nonCompliancesFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonCompliancesComponent implements PendingRequest {
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
              .uncorrectedNonCompliances,
        ),
        mergeMap((uncorrectedNonCompliances) => {
          return iif(
            () => this.form.dirty,
            this.aerService
              .postVerificationTaskSave(
                {
                  uncorrectedNonCompliances: {
                    ...uncorrectedNonCompliances,
                    ...this.form.value,
                    ...(!this.form.get('areThereUncorrectedNonCompliances').value
                      ? { uncorrectedNonCompliances: null }
                      : {}),
                  },
                },
                false,
                'uncorrectedNonCompliances',
              )
              .pipe(map(() => uncorrectedNonCompliances)),
            of(uncorrectedNonCompliances),
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((uncorrectedNonCompliances) => this.nextUrl(uncorrectedNonCompliances));
  }

  private nextUrl(uncorrectedNonCompliances: UncorrectedNonCompliances) {
    return this.form.get('areThereUncorrectedNonCompliances').value
      ? uncorrectedNonCompliances?.uncorrectedNonCompliances?.length > 0
        ? this.router.navigate(['list'], { relativeTo: this.route })
        : this.router.navigate([0], { relativeTo: this.route })
      : this.router.navigate(['summary'], { relativeTo: this.route });
  }
}
