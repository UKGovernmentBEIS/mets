import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { monitoringApproachesFormProvider } from '@tasks/aer/verification-submit/opinion-statement/monitoring-approaches/monitoring-approaches.form.provider';

import { OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-monitoring-approaches',
  templateUrl: './monitoring-approaches.component.html',
  providers: [monitoringApproachesFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachesComponent implements PendingRequest {
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
      this.router.navigate(['../emission-factors'], { relativeTo: this.route }).then();
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
        .subscribe(() => this.router.navigate(['../emission-factors'], { relativeTo: this.route }));
    }
  }
}
