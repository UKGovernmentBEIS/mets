import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { ProcedureFormSummaryComponent } from '@aviation/shared/components/procedure-form-summary';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmpBlockOnBlockOffMethodProcedures } from 'pmrv-api';

import { empQuery } from '../../emp.selectors';
import { EmpReviewDecisionGroupComponent } from '../../emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '../../emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { BlockProceduresFormProvider } from '../block-procedures-form.provider';

interface ViewModel {
  data: EmpBlockOnBlockOffMethodProcedures['fuelConsumptionPerFlight'] | null;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalData: EmpBlockOnBlockOffMethodProcedures['fuelConsumptionPerFlight'] | null;
}

@Component({
  selector: 'app-block-procedures-monitoring-summary',
  templateUrl: './block-procedures-monitoring-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    ProcedureFormSummaryComponent,
    EmpVariationReviewDecisionGroupComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BlockProceduresMonitoringSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('blockOnBlockOffMethodProcedures')),
    this.store.pipe(empQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => {
      return {
        data: this.form.valid
          ? (this.form.value as EmpBlockOnBlockOffMethodProcedures['fuelConsumptionPerFlight'])
          : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'blockOnBlockOffMethodProcedures'),
        isEditable,
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          ['EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT'].includes(type), //TODO consider corsia as well
        showDecision: showReviewDecisionComponent.includes(type),
        showVariationDecision: showVariationReviewDecisionComponent.includes(type),
        showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
        showDiff: !!originalEmpContainer,
        originalData:
          originalEmpContainer?.emissionsMonitoringPlan?.blockOnBlockOffMethodProcedures?.fuelConsumptionPerFlight,
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: BlockProceduresFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (this.form?.valid) {
      const data = {
        fuelConsumptionPerFlight: this.form.value as EmpBlockOnBlockOffMethodProcedures['fuelConsumptionPerFlight'],
      };

      this.store.empDelegate
        .saveEmp({ blockOnBlockOffMethodProcedures: data }, 'complete')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../../..'], { relativeTo: this.route, queryParams: { change: null } });
        });
    }
  }
}
