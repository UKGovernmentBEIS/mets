import { NgFor, NgForOf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, mergeMap, Observable, of } from 'rxjs';

import { empCorsiaQuery } from '@aviation/request-task/emp/shared/emp-corsia.selectors';
import { EmpReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { variationSubmitRegulatorLedRequestTaskTypes } from '@aviation/request-task/emp/shared/util/emp.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSubtaskSummaryValues,
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { ManagementProceduresSummaryTemplateComponent } from '@aviation/shared/components/emp-corsia/management-procedures-summary-template/management-procedures-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { EmpManagementProceduresCorsia } from 'pmrv-api';

import { ManagementProceduresCorsiaFormProvider } from '../management-procedures-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  managementProcedures: EmpManagementProceduresCorsia;
  hideSubmit: boolean;
  dataFlowDiagramFile: {
    fileName: string;
    downloadUrl: string;
  }[];
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision?: boolean;
  showDiff: boolean;
  originalManagementProcedures: EmpManagementProceduresCorsia;
  originalDataFlowDiagramFile: {
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
    NgFor,
    NgForOf,
    EmpVariationReviewDecisionGroupComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
    EmpReviewDecisionGroupComponent,
  ],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empCorsiaQuery.selectStatusForTask('managementProcedures')),
    this.store.pipe(empCorsiaQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => {
      const originalManagementProcedures = originalEmpContainer?.emissionsMonitoringPlan?.managementProcedures;

      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'managementProcedures'),
        isEditable,
        managementProcedures: {
          ...getSubtaskSummaryValues(this.formProvider.form),
          monitoringReportingRoles: this.formProvider.form?.controls?.monitoringReportingRoles?.valid
            ? this.formProvider.getFormValue().monitoringReportingRoles
            : null,
        },
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          variationSubmitRegulatorLedRequestTaskTypes.includes(type),
        dataFlowDiagramFile: this.formProvider.getFormValue()?.dataManagement?.dataFlowDiagram
          ? [
              {
                fileName:
                  this.store.empCorsiaDelegate.payload.empAttachments[
                    this.formProvider.getFormValue().dataManagement?.dataFlowDiagram
                  ],
                downloadUrl: `${this.store.empCorsiaDelegate.baseFileAttachmentDownloadUrl}/${
                  this.formProvider.getFormValue()?.dataManagement?.dataFlowDiagram
                }`,
              },
            ]
          : [],
        showDecision: showReviewDecisionComponent.includes(type),
        showDiff: !!originalEmpContainer,
        originalManagementProcedures: {
          ...originalManagementProcedures,
          monitoringReportingRoles: originalManagementProcedures?.monitoringReportingRoles,
        } as any,
        originalDataFlowDiagramFile: [
          {
            fileName:
              this.store.empDelegate.payload.empAttachments[
                originalManagementProcedures?.dataManagement.dataFlowDiagram
              ],
            downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${originalManagementProcedures?.dataManagement.dataFlowDiagram}`,
          },
        ],
        showVariationDecision: showVariationReviewDecisionComponent.includes(type),
        showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: ManagementProceduresCorsiaFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (this.form.valid) {
      this.store.empCorsiaDelegate
        .saveEmp({ managementProcedures: this.formProvider.getFormValue() }, 'complete')
        .pipe(
          mergeMap(() => {
            return of(this.pendingRequestService.trackRequest());
          }),
        )
        .subscribe(() => {
          this.router.navigate(['../../../../'], { relativeTo: this.route, queryParams: { change: null } });
        });
    }
  }
}
