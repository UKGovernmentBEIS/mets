import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { DataFlowActivitiesFormProvider } from './data-flow-activities-form.provider';

@Component({
  selector: 'app-data-flow-activities',
  templateUrl: './data-flow-activities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DataFlowActivitiesFormProvider],
})
export class DataFlowActivitiesComponent implements PendingRequest {
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onContinue(): void {
    combineLatest([this.permitTask$, this.store])
      .pipe(
        first(),
        switchMap(([permitTask, state]) =>
          this.store.patchTask(
            permitTask,
            {
              ...state.permit.monitoringMethodologyPlans,
              digitizedPlan: {
                ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                procedures: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan?.procedures,
                  ['DATA_FLOW_ACTIVITIES']: this.form.value,
                },
              },
            },
            false,
            'mmpProcedures',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../control-activities'], { relativeTo: this.route }));
  }
}
