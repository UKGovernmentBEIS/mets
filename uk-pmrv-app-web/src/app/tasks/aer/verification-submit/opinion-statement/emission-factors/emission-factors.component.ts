import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { emissionFactorsFormProvider } from '@tasks/aer/verification-submit/opinion-statement/emission-factors/emission-factors.form.provider';

import { OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-emission-factors',
  templateUrl: './emission-factors.component.html',
  providers: [emissionFactorsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionFactorsComponent implements PendingRequest {
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
      this.router.navigate(['../review-emissions'], { relativeTo: this.route }).then();
    } else {
      this.aerService
        .getMappedPayload<OpinionStatement>(['verificationReport', 'opinionStatement'])
        .pipe(
          switchMap((opinionStatement) =>
            this.aerService.postVerificationTaskSave(
              {
                opinionStatement: {
                  ...opinionStatement,
                  ...this.form.value,
                },
              },
              false,
              'opinionStatement',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../review-emissions'], { relativeTo: this.route }));
    }
  }
}
