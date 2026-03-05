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
import { MonitoringApproachCorsiaSummaryTemplateComponent } from '@aviation/shared/components/emp-corsia/monitoring-approach-summary-template/monitoring-approach-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { CertMonitoringApproach } from 'pmrv-api';

import {
  MonitoringApproachCorsiaFormProvider,
  MonitoringApproachCorsiaValues,
} from '../monitoring-approach-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  data: MonitoringApproachCorsiaValues;
  originalData: MonitoringApproachCorsiaValues;
  hideSubmit: boolean;
  dataFlowDiagramFile: {
    fileName: string;
    downloadUrl: string;
  }[];
  originalDataFlowDiagramFile: {
    fileName: string;
    downloadUrl: string;
  }[];
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision?: boolean;
  showDiff: boolean;
}

@Component({
  selector: 'app-monitoring-summary',
  templateUrl: './monitoring-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    MonitoringApproachCorsiaSummaryTemplateComponent,
    NgFor,
    NgForOf,
    EmpVariationReviewDecisionGroupComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
    EmpReviewDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empCorsiaQuery.selectStatusForTask('emissionsMonitoringApproach')),
    this.store.pipe(empCorsiaQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'emissionsMonitoringApproach'),
        isEditable,
        data: getSubtaskSummaryValues(this.formProvider.form),
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          variationSubmitRegulatorLedRequestTaskTypes.includes(type),
        dataFlowDiagramFile:
          this.formProvider.simplifiedApproachForm?.getRawValue()?.supportingEvidenceFiles?.map((doc) => {
            return {
              fileName: doc.file.name,
              downloadUrl: `${this.store.empCorsiaDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
            };
          }) ?? [],
        showDecision: showReviewDecisionComponent.includes(type),
        showDiff: !!originalEmpContainer,
        showVariationDecision: showVariationReviewDecisionComponent.includes(type),
        showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
        originalData: {
          emissionsMonitoringApproach: {
            monitoringApproachType:
              originalEmpContainer?.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType,
            certEmissionsType: (originalEmpContainer?.emissionsMonitoringPlan.emissionsMonitoringApproach as any)
              ?.certEmissionsType,
          },
          simplifiedApproach: {
            explanation: (originalEmpContainer?.emissionsMonitoringPlan.emissionsMonitoringApproach as any)
              ?.explanation,
            supportingEvidenceFiles: (originalEmpContainer?.emissionsMonitoringPlan.emissionsMonitoringApproach as any)
              ?.supportingEvidenceFiles,
          },
        },
        originalDataFlowDiagramFile:
          (
            originalEmpContainer?.emissionsMonitoringPlan.emissionsMonitoringApproach as CertMonitoringApproach
          )?.supportingEvidenceFiles?.map((doc) => {
            return {
              fileName: this.store.empCorsiaDelegate.payload.empAttachments[doc],
              downloadUrl: `${this.store.empCorsiaDelegate.baseFileAttachmentDownloadUrl}/${doc}`,
            };
          }) ?? [],
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: MonitoringApproachCorsiaFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (this.form.valid) {
      this.store.empCorsiaDelegate
        .saveEmp({ emissionsMonitoringApproach: this.formProvider.getFormValue() }, 'complete')
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
