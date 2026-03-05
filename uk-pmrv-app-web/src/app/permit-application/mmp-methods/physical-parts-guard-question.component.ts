import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { PhysicalPartsGuardQuestionFormProvider } from './physical-parts-guard-question-form.provider';

@Component({
  selector: 'app-mmp-physical-parts-guard-question',
  templateUrl: './physical-parts-guard-question.component.html',
  standalone: true,
  imports: [SharedModule, SharedPermitModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [PhysicalPartsGuardQuestionFormProvider],
})
export class PhysicalPartsGuardQuestionComponent implements PendingRequest {
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onContinue(): void {
    const physicalPartsAndUnitsAnswer = this.form.value.physicalPartsAndUnitsAnswer;
    const nextRoute = physicalPartsAndUnitsAnswer
      ? this.store.getValue().permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask?.connections?.length
        ? './physical-parts-list'
        : './physical-parts-list/add-part'
      : './avoid-double-count';

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
                methodTask: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask,
                  physicalPartsAndUnitsAnswer,
                  connections: physicalPartsAndUnitsAnswer
                    ? state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask?.connections
                    : [],
                  assignParts: physicalPartsAndUnitsAnswer
                    ? state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask?.assignParts
                    : null,
                },
              },
            },
            false,
            'mmpMethods',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
  }
}
