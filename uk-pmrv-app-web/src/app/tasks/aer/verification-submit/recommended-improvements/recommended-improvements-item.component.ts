import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { recommendedImprovementsItemFormProvider } from '@tasks/aer/verification-submit/recommended-improvements/recommended-improvements-item-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload, RecommendedImprovements, VerifierComment } from 'pmrv-api';

@Component({
  selector: 'app-recommended-improvements-item',
  templateUrl: './recommended-improvements-item.component.html',
  providers: [recommendedImprovementsItemFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsItemComponent implements PendingRequest {
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
            const improvements = (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
              .recommendedImprovements;
            const recommendedImprovementItems: VerifierComment[] =
              index >= (improvements?.recommendedImprovements?.length ?? 0)
                ? [...(improvements?.recommendedImprovements ?? []), { ...this.form.value, reference: `D${index + 1}` }]
                : improvements.recommendedImprovements.map((item, idx) =>
                    idx === index ? { ...item, ...this.form.value } : item,
                  );

            return this.aerService.postVerificationTaskSave(
              {
                recommendedImprovements: {
                  ...improvements,
                  recommendedImprovements: recommendedImprovementItems,
                } as RecommendedImprovements,
              },
              false,
              'recommendedImprovements',
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
