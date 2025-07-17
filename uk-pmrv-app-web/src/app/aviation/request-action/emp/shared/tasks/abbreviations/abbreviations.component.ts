import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { EmpSubmittedViewModel, getEmpSubmittedViewModelData } from '@aviation/request-action/emp/util/emp.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { EmpVariationRegulatorLedDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-regulator-led-decision-group-summary/emp-variation-regulator-led-decision-group-summary.component';
import { EmpVariationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-review-decision-group-summary/emp-variation-review-decision-group-summary.component';
import { SharedModule } from '@shared/shared.module';

import { EmpAbbreviations } from 'pmrv-api';

interface ViewModel {
  data: EmpAbbreviations;
}

@Component({
  selector: 'app-abbreviations',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    EmpReviewDecisionGroupSummaryComponent,
    EmpVariationRegulatorLedDecisionGroupSummaryComponent,
    EmpVariationReviewDecisionGroupSummaryComponent,
  ],
  templateUrl: './abbreviations.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AbbreviationsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & EmpSubmittedViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, regulatorViewer, requestActionType]) => ({
      data: payload.emissionsMonitoringPlan.abbreviations,
      ...getEmpSubmittedViewModelData(
        requestActionType,
        payload,
        regulatorViewer,
        this.store.empDelegate.baseFileAttachmentDownloadUrl,
        'abbreviations',
      ),
    })),
  );
}
