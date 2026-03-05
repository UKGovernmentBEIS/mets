import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { EmpSubmittedViewModel, getEmpSubmittedViewModelData } from '@aviation/request-action/emp/util/emp.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/emp/data-gaps/data-gaps-summary-template.component';
import { extractMonitorinApproachType } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { EmpVariationRegulatorLedDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-regulator-led-decision-group-summary/emp-variation-regulator-led-decision-group-summary.component';
import { EmpVariationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-review-decision-group-summary/emp-variation-review-decision-group-summary.component';
import { DataGapsCorsiaSummaryTemplateComponent } from '@aviation/shared/components/emp-corsia/data-gaps/data-gaps-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { EmpDataGaps } from 'pmrv-api';

interface ViewModel {
  data: EmpDataGaps;
  isCorsia: boolean;
  monitoringApproachType: any;
}

@Component({
  selector: 'app-data-gaps',
  standalone: true,
  imports: [
    RequestActionTaskComponent,
    EmpReviewDecisionGroupSummaryComponent,
    DataGapsSummaryTemplateComponent,
    DataGapsCorsiaSummaryTemplateComponent,
    EmpVariationRegulatorLedDecisionGroupSummaryComponent,
    SharedModule,
    EmpVariationReviewDecisionGroupSummaryComponent,
  ],
  templateUrl: './data-gaps.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & EmpSubmittedViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectIsCorsia),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, isCorsia, regulatorViewer, requestActionType]) => ({
      data: payload.emissionsMonitoringPlan.dataGaps,
      isCorsia,
      monitoringApproachType: extractMonitorinApproachType(payload.emissionsMonitoringPlan),
      ...getEmpSubmittedViewModelData(
        requestActionType,
        payload,
        regulatorViewer,
        this.store.empDelegate.baseFileAttachmentDownloadUrl,
        'dataGaps',
      ),
    })),
  );
}
