import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { empCorsiaQuery } from '@aviation/request-task/emp/shared/emp-corsia.selectors';
import { EmpReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { DataGapsCorsiaSummaryTemplateComponent } from '@aviation/shared/components/emp-corsia/data-gaps/data-gaps-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmpDataGapsCorsia, EmpEmissionsMonitoringApproachCorsia } from 'pmrv-api';

import { EmpVariationReviewDecisionGroupComponent } from '../../../../shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { DataGapsFormModel } from '../data-gaps-form.model';

interface ViewModel {
  data: EmpDataGapsCorsia;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalData: EmpDataGapsCorsia;
  monitoringApproachType: EmpEmissionsMonitoringApproachCorsia['monitoringApproachType'];
}

@Component({
  selector: 'app-data-gaps-summary',
  templateUrl: './data-gaps-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    DataGapsCorsiaSummaryTemplateComponent,
    EmpVariationReviewDecisionGroupComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsSummaryComponent {
  constructor(
    private store: RequestTaskStore,
    @Inject(TASK_FORM_PROVIDER) private form: DataGapsFormModel,
    private route: ActivatedRoute,
    private pendingRequestService: PendingRequestService,
    private router: Router,
  ) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empCorsiaQuery.selectStatusForTask('dataGaps')),
    this.store.pipe(empCorsiaQuery.selectOriginalEmpContainer),
    this.store.pipe(empCorsiaQuery.selectMonitoringApproachType),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer, monitoringApproachType]) => ({
      data: this.form.getRawValue() as EmpDataGapsCorsia,
      pageHeader: getSummaryHeaderForTaskType(type, 'dataGaps'),
      isEditable,
      hideSubmit:
        !isEditable ||
        ['complete', 'cannot start yet'].includes(taskStatus) ||
        [
          'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW',
          'EMP_VARIATION_CORSIA_APPLICATION_REVIEW',
          'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT',
        ].includes(type),
      showDecision: showReviewDecisionComponent.includes(type),
      showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
      showDiff: !!originalEmpContainer,
      originalData: originalEmpContainer?.emissionsMonitoringPlan?.dataGaps,
      monitoringApproachType,
    })),
  );
  onSubmit() {
    (this.store.empDelegate as EmpCorsiaStoreDelegate)
      .saveEmp({ dataGaps: this.form.getRawValue() }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => this.router.navigate(['../../../..'], { relativeTo: this.route }));
  }
}
