import { ChangeDetectionStrategy, Component } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { reviewRequestTaskTypes } from '@permit-application/shared/utils/permit';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { MmpEnergyFlowsSummaryTemplateComponent } from './mmp-energy-flows-summary-template/mmp-energy-flows-summary-template.component';

@Component({
  selector: 'app-mmp-energy-flows-summary',
  templateUrl: './mmp-energy-flows-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [MmpEnergyFlowsSummaryTemplateComponent, SharedModule, SharedPermitModule, RouterModule],
})
export class MmpEnergyFlowsSummaryComponent implements PendingRequest {
  state = toSignal(this.store.asObservable());
  isReview = reviewRequestTaskTypes.includes(this.state()?.requestTaskType);
  hideSubmit$ = combineLatest([this.store.isEditable$]).pipe(
    map(([isEditable]) => {
      return !isEditable || this.store.getState().permitSectionsCompleted?.mmpEnergyFlows?.[0];
    }),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,
    readonly pendingRequest: PendingRequestService,
    readonly route: ActivatedRoute,
  ) {}

  confirm(): void {
    combineLatest([this.store, this.route.data])
      .pipe(
        first(),
        switchMap(([state, data]) => {
          const digitizedPlan = state.permit?.monitoringMethodologyPlans?.digitizedPlan;
          const energyFlows = digitizedPlan && digitizedPlan?.energyFlows;
          const energyFlowsCount = Object.keys(energyFlows)?.length;
          return this.store.postStatus('mmpEnergyFlows', energyFlowsCount === 4 ? true : false, data.permitTask);
        }),
      )
      .subscribe(() =>
        this.router.navigate([this.isReview ? '../../review/monitoring-methodology-plan' : '../../'], {
          relativeTo: this.route,
          state: { notification: true },
        }),
      );
  }
}
