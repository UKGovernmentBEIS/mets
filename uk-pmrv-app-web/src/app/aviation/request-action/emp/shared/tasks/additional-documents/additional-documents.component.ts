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

import { EmpAdditionalDocuments } from 'pmrv-api';

interface ViewModel {
  hasDocuments: boolean;
  files: { downloadUrl: string; fileName: string }[];
  originalFiles: { downloadUrl: string; fileName: string }[];
  additionalDocuments: EmpAdditionalDocuments;
}

@Component({
  selector: 'app-additional-documents',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    EmpReviewDecisionGroupSummaryComponent,
    EmpVariationRegulatorLedDecisionGroupSummaryComponent,
    EmpVariationReviewDecisionGroupSummaryComponent,
  ],
  templateUrl: './additional-documents.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & EmpSubmittedViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, regulatorViewer, requestActionType]) => ({
      hasDocuments: payload.emissionsMonitoringPlan.additionalDocuments.exist,
      files:
        payload.emissionsMonitoringPlan.additionalDocuments.documents?.map((uuid) => {
          const file = payload.empAttachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      originalFiles:
        payload.originalEmpContainer?.emissionsMonitoringPlan.additionalDocuments.documents?.map((uuid) => {
          const file = payload.originalEmpContainer.empAttachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      additionalDocuments: payload.emissionsMonitoringPlan.additionalDocuments,
      ...getEmpSubmittedViewModelData(
        requestActionType,
        payload,
        regulatorViewer,
        this.store.empDelegate.baseFileAttachmentDownloadUrl,
        'additionalDocuments',
      ),
    })),
  );
}
