import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { reviewRequestTaskTypes } from '@permit-application/shared/utils/permit';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  createPhysicalPartsListForm,
  hasTwoOrMoreSubInstallationsCompleted,
  isMethodsWizardCompleted,
  mmpMethodsStatus,
} from '../mmp-methods';
import { MethodsSummaryTemplateComponent } from './methods-summary-template/methods-summary-template.component';

interface ViewModel {
  isEditable: boolean;
  showSubmitButton: boolean;
}

@Component({
  selector: 'app-mmp-methods-summary',
  standalone: true,
  imports: [MethodsSummaryTemplateComponent, SharedPermitModule, SharedModule],
  templateUrl: './methods-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MethodsSummaryComponent {
  routeData = toSignal(this.route.data);
  state = toSignal(this.store.asObservable());
  hasTwoOrMoreSubInstallationsCompleted = hasTwoOrMoreSubInstallationsCompleted(this.state());
  form = createPhysicalPartsListForm(this.state());
  isReview = reviewRequestTaskTypes.includes(this.state()?.requestTaskType);

  vm: Signal<ViewModel> = computed(() => {
    const state = this.state();
    const methodTask = state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask;
    const isFormValid = methodTask.physicalPartsAndUnitsAnswer ? this.form.valid : true;
    const isInProgressOrNeedsReview = (['in progress', 'needs review'] as Array<TaskItemStatus>).includes(
      mmpMethodsStatus(state),
    );

    return {
      isEditable: state.isEditable,
      showSubmitButton: state.isEditable && isInProgressOrNeedsReview && isFormValid,
    };
  });

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  confirm() {
    const state = this.state();
    const data = this.routeData();

    this.store
      .postStatus('mmpMethods', isMethodsWizardCompleted(state), data.permitTask, {
        ...state.permit,
        monitoringMethodologyPlans: {
          ...state.permit.monitoringMethodologyPlans,
          digitizedPlan: {
            ...state.permit.monitoringMethodologyPlans.digitizedPlan,
            methodTask: {
              ...state.permit.monitoringMethodologyPlans.digitizedPlan.methodTask,
              physicalPartsAndUnitsAnswer: this.hasTwoOrMoreSubInstallationsCompleted
                ? state.permit.monitoringMethodologyPlans.digitizedPlan.methodTask.physicalPartsAndUnitsAnswer
                : null,
              assignParts: this.hasTwoOrMoreSubInstallationsCompleted
                ? state.permit.monitoringMethodologyPlans.digitizedPlan.methodTask.assignParts
                : null,
              connections: this.hasTwoOrMoreSubInstallationsCompleted
                ? state.permit.monitoringMethodologyPlans.digitizedPlan.methodTask.connections
                : null,
            },
          },
        },
      })
      .subscribe(() =>
        this.router.navigate([this.isReview ? '../../review/monitoring-methodology-plan' : '../../'], {
          relativeTo: this.route,
        }),
      );
  }
}
