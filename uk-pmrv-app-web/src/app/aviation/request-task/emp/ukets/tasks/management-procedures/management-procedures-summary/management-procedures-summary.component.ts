import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import {
  issuanceReviewRequestTaskTypes,
  variationOperatorLedReviewRequestTaskTypes,
  variationSubmitRegulatorLedRequestTaskTypes,
} from '@aviation/request-task/emp/shared/util/emp.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import {
  getSubtaskSummaryValues,
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { ManagementProceduresSummaryTemplateComponent } from '@aviation/shared/components/emp/management-procedures-summary-template/management-procedures-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmpManagementProcedures } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { empQuery } from '../../../../shared/emp.selectors';
import { EmpReviewDecisionGroupComponent } from '../../../../shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '../../../../shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  managementProcedures: EmpManagementProcedures;
  diagramAttachmentFiles: {
    fileName: string;
    downloadUrl: string;
  }[];
  riskAssessmentFiles: {
    fileName: string;
    downloadUrl: string;
  }[];
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalManagementProcedures: EmpManagementProcedures;
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
  selector: 'app-management-procedures-summary',
  templateUrl: './management-procedures-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    ManagementProceduresSummaryTemplateComponent,
    EmpVariationReviewDecisionGroupComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresSummaryComponent {
  private store = inject(RequestTaskStore);
  private formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('managementProcedures')),
    this.store.pipe(empQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => ({
      pageHeader: getSummaryHeaderForTaskType(type, 'managementProcedures'),
      isEditable,
      managementProcedures: {
        ...getSubtaskSummaryValues(this.formProvider.form),
        monitoringReportingRoles: this.formProvider.form?.controls?.monitoringReportingRoles?.valid
          ? this.formProvider.getFormValue().monitoringReportingRoles
          : null,
      },
      diagramAttachmentFiles: this.formProvider.getFormValue()?.dataFlowActivities?.diagramAttachmentId
        ? [
            {
              fileName:
                this.store.empUkEtsDelegate.payload.empAttachments[
                  this.formProvider.getFormValue().dataFlowActivities?.diagramAttachmentId
                ],
              downloadUrl: `${this.store.empUkEtsDelegate.baseFileAttachmentDownloadUrl}/${
                this.formProvider.getFormValue()?.dataFlowActivities?.diagramAttachmentId
              }`,
            },
          ]
        : [],
      riskAssessmentFiles: this.formProvider.getFormValue()?.riskAssessmentFile
        ? [
            {
              fileName:
                this.store.empUkEtsDelegate.payload.empAttachments[this.formProvider.getFormValue().riskAssessmentFile],
              downloadUrl: `${this.store.empUkEtsDelegate.baseFileAttachmentDownloadUrl}/${
                this.formProvider.getFormValue()?.riskAssessmentFile
              }`,
            },
          ]
        : [],
      hideSubmit:
        !isEditable ||
        ['complete', 'cannot start yet'].includes(taskStatus) ||
        ['EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT'].includes(type) ||
        variationSubmitRegulatorLedRequestTaskTypes.includes(type) ||
        variationOperatorLedReviewRequestTaskTypes.includes(type) ||
        issuanceReviewRequestTaskTypes.includes(type),
      showDecision: showReviewDecisionComponent.includes(type),
      showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
      showDiff: !!originalEmpContainer,
      originalManagementProcedures: originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures,
      originalDiagramAttachmentFiles: originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures
        ?.dataFlowActivities?.diagramAttachmentId
        ? [
            {
              fileName:
                originalEmpContainer?.empAttachments[
                  originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures?.dataFlowActivities
                    ?.diagramAttachmentId
                ],
              downloadUrl: `${this.store.empUkEtsDelegate.baseFileAttachmentDownloadUrl}/${originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures?.dataFlowActivities?.diagramAttachmentId}`,
            },
          ]
        : [],
      originalRiskAssessmentFiles: originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures
        ?.riskAssessmentFile
        ? [
            {
              fileName:
                originalEmpContainer?.empAttachments[
                  originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures?.riskAssessmentFile
                ],
              downloadUrl: `${this.store.empUkEtsDelegate.baseFileAttachmentDownloadUrl}/${originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures?.riskAssessmentFile}`,
            },
          ]
        : [],
    })),
  );

  onSubmit() {
    this.store.empUkEtsDelegate
      .saveEmp({ managementProcedures: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => this.router.navigate(['../../../..'], { relativeTo: this.route }));
  }
}
