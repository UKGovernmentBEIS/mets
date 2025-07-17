import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { ControlActivitiesFormProvider } from './control-activities-form.provider';

@Component({
  selector: 'app-control-activities',
  templateUrl: './control-activities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ControlActivitiesFormProvider],
})
export class ControlActivitiesComponent implements PendingRequest {
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
                  ['CONTROL_ACTIVITIES']: this.form.value,
                },
              },
            },
            false,
            'mmpProcedures',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route }));
  }
}
