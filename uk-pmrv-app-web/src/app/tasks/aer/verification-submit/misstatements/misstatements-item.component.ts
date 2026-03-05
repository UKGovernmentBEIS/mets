import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { misstatementsItemFormProvider } from '@tasks/aer/verification-submit/misstatements/misstatements-item-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-misstatements-item',
  templateUrl: './misstatements-item.component.html',
  providers: [misstatementsItemFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisstatementsItemComponent {
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
            const misstatementsInfo = (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
              .uncorrectedMisstatements;

            return this.aerService.postVerificationTaskSave(
              {
                uncorrectedMisstatements: {
                  ...misstatementsInfo,
                  uncorrectedMisstatements:
                    index >= (misstatementsInfo?.uncorrectedMisstatements?.length ?? 0)
                      ? [
                          ...(misstatementsInfo?.uncorrectedMisstatements ?? []),
                          { ...this.form.value, reference: `A${index + 1}` },
                        ]
                      : misstatementsInfo.uncorrectedMisstatements.map((item, idx) =>
                          idx === index ? { ...item, ...this.form.value } : item,
                        ),
                },
              },
              false,
              'uncorrectedMisstatements',
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
