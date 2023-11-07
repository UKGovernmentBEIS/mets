import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, iif, map, mergeMap, of } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { misstatementsFormProvider } from '@tasks/aer/verification-submit/misstatements/misstatements-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload, UncorrectedMisstatements } from 'pmrv-api';

@Component({
  selector: 'app-misstatements',
  templateUrl: './misstatements.component.html',
  providers: [misstatementsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisstatementsComponent implements PendingRequest {
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
            (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.uncorrectedMisstatements,
        ),
        mergeMap((misstatementsInfo) =>
          iif(
            () => this.form.dirty,
            this.aerService
              .postVerificationTaskSave(
                {
                  uncorrectedMisstatements: {
                    ...this.form.value,
                    ...(!this.form.get('areThereUncorrectedMisstatements').value
                      ? { uncorrectedMisstatements: null }
                      : {}),
                  },
                },
                false,
                'uncorrectedMisstatements',
              )
              .pipe(map(() => misstatementsInfo)),
            of(misstatementsInfo),
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((misstatementsInfo) => this.nextUrl(misstatementsInfo));
  }

  private nextUrl(misstatementsInfo: UncorrectedMisstatements) {
    return this.form.get('areThereUncorrectedMisstatements').value
      ? misstatementsInfo?.uncorrectedMisstatements?.length > 0
        ? this.router.navigate(['list'], { relativeTo: this.route })
        : this.router.navigate([0], { relativeTo: this.route })
      : this.router.navigate(['summary'], { relativeTo: this.route });
  }
}
