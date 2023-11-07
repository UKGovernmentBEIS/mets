import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { monitoringPlanFormFactory } from './monitoring-plan-form.provider';

@Component({
  selector: 'app-additional-documents',
  templateUrl: './monitoring-plan.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [monitoringPlanFormFactory],
})
export class MonitoringPlanComponent {
  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  monitoringPlanVersions$ = this.aerService
    .getPayload()
    .pipe(map((payload: AerApplicationSubmitRequestTaskPayload) => payload.monitoringPlanVersions));

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['summary'], { relativeTo: this.route });
    } else {
      this.aerService
        .postTaskSave(
          {
            aerMonitoringPlanDeviation: {
              ...this.form.value,
            },
          },
          undefined,
          false,
          'aerMonitoringPlanDeviation',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
    }
  }
}
