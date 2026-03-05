import { ChangeDetectionStrategy, Component } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { reviewRequestTaskTypes } from '@permit-application/shared/utils/permit';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { MmpProceduresSummaryTemplateComponent } from './mmp-procedures-summary-template/mmp-procedures-summary-template.component';

@Component({
  selector: 'app-mmp-procedures-summary',
  templateUrl: './mmp-procedures-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedPermitModule, SharedModule, MmpProceduresSummaryTemplateComponent],
})
export class MmpProceduresSummaryComponent extends ProductBenchmarkComponent implements PendingRequest {
  state = toSignal(this.store.asObservable());
  isReview = reviewRequestTaskTypes.includes(this.state()?.requestTaskType);
  hideSubmit$ = combineLatest([this.store.isEditable$]).pipe(
    map(([isEditable]) => {
      return !isEditable || this.store.getState().permitSectionsCompleted?.mmpProcedures?.[0];
    }),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,
    readonly pendingRequest: PendingRequestService,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  confirm(): void {
    combineLatest([this.store, this.route.data])
      .pipe(
        first(),
        switchMap(([state, data]) => {
          const digitizedPlan = state.permit?.monitoringMethodologyPlans?.digitizedPlan;
          const procedures = digitizedPlan && digitizedPlan?.procedures;
          const proceduresCount = Object.keys(procedures)?.length;
          return this.store.postStatus('mmpProcedures', proceduresCount === 4 ? true : false, data.permitTask);
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
