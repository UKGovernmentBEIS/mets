import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { additionalChangesFormProvider } from '@tasks/aer/verification-submit/opinion-statement/additional-changes/additional-changes.form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload, OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-additional-changes',
  templateUrl: './additional-changes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [additionalChangesFormProvider],
})
export class AdditionalChangesComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;
  payload$ = this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../site-visits'], { relativeTo: this.route }).then();
    } else {
      this.aerService
        .getMappedPayload<OpinionStatement>(['verificationReport', 'opinionStatement'])
        .pipe(
          switchMap((opinionStatement) =>
            this.aerService.postVerificationTaskSave(
              {
                opinionStatement: {
                  ...opinionStatement,
                  additionalChangesNotCovered: this.form.get('additionalChangesNotCovered').value,
                  additionalChangesNotCoveredDetails: this.form.get('additionalChangesNotCovered').value
                    ? this.form.get('additionalChangesNotCoveredDetails').value
                    : null,
                },
              },
              false,
              'opinionStatement',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../site-visits'], { relativeTo: this.route }));
    }
  }
}
