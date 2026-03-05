import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { EmpSubmittedViewModel, getEmpSubmittedViewModelData } from '@aviation/request-action/emp/util/emp.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { MonitoringApproachCorsiaValues } from '@aviation/request-task/emp/corsia/tasks/monitoring-approach/monitoring-approach-form.provider';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { EmpVariationRegulatorLedDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-regulator-led-decision-group-summary/emp-variation-regulator-led-decision-group-summary.component';
import { EmpVariationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-review-decision-group-summary/emp-variation-review-decision-group-summary.component';
import { MonitoringApproachSummaryTemplateComponent } from '@aviation/shared/components/emp/monitoring-approach-summary-template/monitoring-approach-summary-template.component';
import { MonitoringApproachCorsiaSummaryTemplateComponent } from '@aviation/shared/components/emp-corsia/monitoring-approach-summary-template/monitoring-approach-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { CertMonitoringApproach, SmallEmittersMonitoringApproach, SupportFacilityMonitoringApproach } from 'pmrv-api';

interface ViewModel {
  data: SmallEmittersMonitoringApproach | SupportFacilityMonitoringApproach | MonitoringApproachCorsiaValues;
  files: { downloadUrl: string; fileName: string }[];
  originalFiles: { downloadUrl: string; fileName: string }[];
  isCorsia: boolean;
}

@Component({
  selector: 'app-monitoring-approach',
  standalone: true,
  imports: [
    RequestActionTaskComponent,
    EmpReviewDecisionGroupSummaryComponent,
    MonitoringApproachSummaryTemplateComponent,
    MonitoringApproachCorsiaSummaryTemplateComponent,
    EmpVariationRegulatorLedDecisionGroupSummaryComponent,
    SharedModule,
    EmpVariationReviewDecisionGroupSummaryComponent,
  ],
  templateUrl: './monitoring-approach.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & EmpSubmittedViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectIsCorsia),
  ]).pipe(
    map(([payload, regulatorViewer, requestActionType, isCorsia]) => ({
      isCorsia,
      data: !isCorsia
        ? (payload.emissionsMonitoringPlan.emissionsMonitoringApproach as
            | SmallEmittersMonitoringApproach
            | SupportFacilityMonitoringApproach)
        : ({
            emissionsMonitoringApproach: {
              monitoringApproachType:
                payload.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType,
              certEmissionsType: (payload.emissionsMonitoringPlan.emissionsMonitoringApproach as CertMonitoringApproach)
                ?.certEmissionsType,
            },
            simplifiedApproach: {
              explanation: (payload.emissionsMonitoringPlan.emissionsMonitoringApproach as CertMonitoringApproach)
                ?.explanation,
              supportingEvidenceFiles: (
                payload.emissionsMonitoringPlan.emissionsMonitoringApproach as CertMonitoringApproach
              )?.supportingEvidenceFiles,
            },
          } as MonitoringApproachCorsiaValues),

      files:
        (
          payload.emissionsMonitoringPlan.emissionsMonitoringApproach as
            | SmallEmittersMonitoringApproach
            | SupportFacilityMonitoringApproach
            | CertMonitoringApproach
        )?.supportingEvidenceFiles?.map((uuid) => {
          const file = payload.empAttachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      originalFiles:
        (
          payload.originalEmpContainer?.emissionsMonitoringPlan.emissionsMonitoringApproach as
            | SmallEmittersMonitoringApproach
            | SupportFacilityMonitoringApproach
            | CertMonitoringApproach
        )?.supportingEvidenceFiles?.map((uuid) => {
          const file = payload.originalEmpContainer.empAttachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      ...getEmpSubmittedViewModelData(
        requestActionType,
        payload,
        regulatorViewer,
        this.store.empDelegate.baseFileAttachmentDownloadUrl,
        'emissionsMonitoringApproach',
      ),
      originalData: isCorsia
        ? ({
            emissionsMonitoringApproach: {
              monitoringApproachType:
                payload.originalEmpContainer?.emissionsMonitoringPlan.emissionsMonitoringApproach
                  .monitoringApproachType,
              certEmissionsType: (
                payload.originalEmpContainer?.emissionsMonitoringPlan
                  .emissionsMonitoringApproach as CertMonitoringApproach
              )?.certEmissionsType,
            },
            simplifiedApproach: {
              explanation: (
                payload.originalEmpContainer?.emissionsMonitoringPlan
                  .emissionsMonitoringApproach as CertMonitoringApproach
              )?.explanation,
              supportingEvidenceFiles: (
                payload.originalEmpContainer?.emissionsMonitoringPlan
                  .emissionsMonitoringApproach as CertMonitoringApproach
              )?.supportingEvidenceFiles,
            },
          } as MonitoringApproachCorsiaValues)
        : payload.originalEmpContainer?.emissionsMonitoringPlan?.emissionsMonitoringApproach,
    })),
  );
}
