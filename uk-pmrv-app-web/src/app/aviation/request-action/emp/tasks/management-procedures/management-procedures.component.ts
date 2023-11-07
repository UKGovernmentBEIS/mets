import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { EmpSubmittedViewModel, getEmpSubmittedViewModelData } from '@aviation/request-action/emp/util/emp.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { EmpVariationRegulatorLedDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-regulator-led-decision-group-summary/emp-variation-regulator-led-decision-group-summary.component';
import { EmpVariationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-review-decision-group-summary/emp-variation-review-decision-group-summary.component';
import { ManagementProceduresSummaryTemplateComponent } from '@aviation/shared/components/emp/management-procedures-summary-template';
import { SharedModule } from '@shared/shared.module';

import { EmpManagementProcedures } from 'pmrv-api';

interface ViewModel {
  data: EmpManagementProcedures;
  diagramAttachmentFiles: {
    fileName: string;
    downloadUrl: string;
  }[];
  riskAssessmentFiles: {
    fileName: string;
    downloadUrl: string;
  }[];
  originalDiagramAttachmentFiles: {
    fileName: string;
    downloadUrl: string;
  }[];
  originalRiskAssessmentFiles: {
    fileName: string;
    downloadUrl: string;
  }[];
}

@Component({
  selector: 'app-management-procedures',
  standalone: true,
  imports: [
    RequestActionTaskComponent,
    EmpReviewDecisionGroupSummaryComponent,
    ManagementProceduresSummaryTemplateComponent,
    EmpVariationRegulatorLedDecisionGroupSummaryComponent,
    SharedModule,
    EmpVariationReviewDecisionGroupSummaryComponent,
  ],
  templateUrl: './management-procedures.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & EmpSubmittedViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, regulatorViewer, requestActionType]) => ({
      data: payload.emissionsMonitoringPlan.managementProcedures,
      diagramAttachmentFiles: payload.emissionsMonitoringPlan.managementProcedures?.dataFlowActivities
        ?.diagramAttachmentId
        ? [
            {
              fileName:
                this.store.empDelegate.payload.empAttachments[
                  payload.emissionsMonitoringPlan.managementProcedures.dataFlowActivities?.diagramAttachmentId
                ],
              downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${payload.emissionsMonitoringPlan.managementProcedures.dataFlowActivities?.diagramAttachmentId}`,
            },
          ]
        : [],
      riskAssessmentFiles: payload.emissionsMonitoringPlan.managementProcedures?.riskAssessmentFile
        ? [
            {
              fileName:
                this.store.empDelegate.payload.empAttachments[
                  payload.emissionsMonitoringPlan.managementProcedures.riskAssessmentFile
                ],
              downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${payload.emissionsMonitoringPlan.managementProcedures.riskAssessmentFile}`,
            },
          ]
        : [],
      originalDiagramAttachmentFiles: payload.originalEmpContainer?.emissionsMonitoringPlan.managementProcedures
        .dataFlowActivities?.diagramAttachmentId
        ? [
            {
              fileName:
                payload.originalEmpContainer?.empAttachments[
                  payload.originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures?.dataFlowActivities
                    ?.diagramAttachmentId
                ],
              downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${payload.originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures?.dataFlowActivities?.diagramAttachmentId}`,
            },
          ]
        : [],
      originalRiskAssessmentFiles: payload.originalEmpContainer?.emissionsMonitoringPlan.managementProcedures
        ?.riskAssessmentFile
        ? [
            {
              fileName:
                payload.originalEmpContainer?.empAttachments[
                  payload.originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures?.riskAssessmentFile
                ],
              downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${payload.originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures?.riskAssessmentFile}`,
            },
          ]
        : [],
      ...getEmpSubmittedViewModelData(
        requestActionType,
        payload,
        regulatorViewer,
        this.store.empDelegate.baseFileAttachmentDownloadUrl,
        'managementProcedures',
      ),
    })),
  );
}
