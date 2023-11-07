import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { materialityLevelFormProvider } from '@tasks/aer/verification-submit/materiality-level/materiality-level-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-materiality-level',
  templateUrl: './materiality-level.component.html',
  providers: [materialityLevelFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MaterialityLevelComponent {
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
      this.router.navigate(['reference-documents'], { relativeTo: this.route });
    } else {
      this.aerService
        .getPayload()
        .pipe(
          first(),
          map(
            (payload) =>
              (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
                .materialityLevel,
          ),
          switchMap((materialityLevelInfo) =>
            this.aerService.postVerificationTaskSave(
              {
                materialityLevel: {
                  ...materialityLevelInfo,
                  ...this.form.value,
                },
              },
              false,
              'materialityLevel',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['reference-documents'], { relativeTo: this.route }));
    }
  }
}
