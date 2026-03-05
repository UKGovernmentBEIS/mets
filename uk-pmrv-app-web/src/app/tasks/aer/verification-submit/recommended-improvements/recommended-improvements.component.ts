import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, iif, map, mergeMap, of } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { recommendedImprovementsFormProvider } from '@tasks/aer/verification-submit/recommended-improvements/recommended-improvements-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload, RecommendedImprovements } from 'pmrv-api';

@Component({
  selector: 'app-recommended-improvements',
  templateUrl: './recommended-improvements.component.html',
  providers: [recommendedImprovementsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsComponent implements PendingRequest {
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
            (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.recommendedImprovements,
        ),
        mergeMap((recommendedImprovements) => {
          return iif(
            () => this.form.dirty,
            this.aerService
              .postVerificationTaskSave(
                {
                  recommendedImprovements: {
                    ...recommendedImprovements,
                    ...this.form.value,
                    ...(!this.form.get('areThereRecommendedImprovements').value
                      ? { recommendedImprovements: null }
                      : {}),
                  },
                },
                false,
                'recommendedImprovements',
              )
              .pipe(map(() => recommendedImprovements)),
            of(recommendedImprovements),
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((recommendedImprovements) => this.nextUrl(recommendedImprovements));
  }

  private nextUrl(recommendedImprovements: RecommendedImprovements) {
    return this.form.get('areThereRecommendedImprovements').value
      ? recommendedImprovements?.recommendedImprovements?.length > 0
        ? this.router.navigate(['list'], { relativeTo: this.route })
        : this.router.navigate([0], { relativeTo: this.route })
      : this.router.navigate(['summary'], { relativeTo: this.route });
  }
}
