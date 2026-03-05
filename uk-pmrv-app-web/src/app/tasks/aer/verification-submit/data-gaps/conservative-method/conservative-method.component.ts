import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { conservativeMethodFormProvider } from '@tasks/aer/verification-submit/data-gaps/conservative-method/conservative-method-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-conservative-method',
  templateUrl: './conservative-method.component.html',
  providers: [conservativeMethodFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConservativeMethodComponent {
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
      this.nextUrl();
    } else {
      this.aerService
        .getPayload()
        .pipe(
          first(),
          map(
            (payload) =>
              (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
                .methodologiesToCloseDataGaps,
          ),
          switchMap((dataGapsInfo) =>
            this.aerService.postVerificationTaskSave(
              {
                methodologiesToCloseDataGaps: {
                  ...dataGapsInfo,
                  dataGapRequiredDetails: {
                    ...dataGapsInfo.dataGapRequiredDetails,
                    dataGapApprovedDetails: {
                      ...dataGapsInfo.dataGapRequiredDetails.dataGapApprovedDetails,
                      ...this.form.value,
                      ...(this.form.get('conservativeMethodUsed').value ? { methodDetails: null } : {}),
                    },
                  },
                },
              },
              false,
              'methodologiesToCloseDataGaps',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.nextUrl());
    }
  }

  private nextUrl() {
    return this.router.navigate(['../material-misstatement'], { relativeTo: this.route });
  }
}
