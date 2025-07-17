import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { AssignmentOfResponsibilitiesFormProvider } from './assignment-of-responsibilities-form.provider';

@Component({
  selector: 'app-assignment-of-responsibilities',
  templateUrl: './assignment-of-responsibilities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [AssignmentOfResponsibilitiesFormProvider],
})
export class AssignmentOfResponsibilitiesComponent implements PendingRequest {
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
                  ['ASSIGNMENT_OF_RESPONSIBILITIES']: this.form.value,
                },
              },
            },
            false,
            'mmpProcedures',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['./monitoring-plan-appropriateness'], { relativeTo: this.route }));
  }
}
