import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { requestTaskQuery } from '@aviation/request-task/store';
import {
  getSubtaskSummaryValues,
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { MonitoringApproachSummaryTemplateComponent } from '@aviation/shared/components/emp/monitoring-approach-summary-template/monitoring-approach-summary-template.component';
import { SimplifiedMonitoringApproach } from '@aviation/shared/components/emp/monitoring-approach-summary-template/monitoring-approach-types.interface';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { MonitoringApproachTypePipe } from '@aviation/shared/pipes/monitoring-approach-type.pipe';
import { SharedModule } from '@shared/shared.module';

import {
  EmpEmissionsMonitoringApproach,
  SmallEmittersMonitoringApproach,
  SupportFacilityMonitoringApproach,
} from 'pmrv-api';

import { empQuery } from '../../../../shared/emp.selectors';
import { EmpReviewDecisionGroupComponent } from '../../../../shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '../../../../shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { BaseMonitoringApproachComponent } from '../base-monitoring-approach.component';

interface ViewModel {
  emissionsMonitoringApproach: SimplifiedMonitoringApproach;
  files: { downloadUrl: string; fileName: string }[];
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalemissionsMonitoringApproach: EmpEmissionsMonitoringApproach;
  originalFiles: { downloadUrl: string; fileName: string }[];
}

@Component({
  selector: 'app-monitoring-summary',
  templateUrl: './monitoring-summary.component.html',
  standalone: true,
  imports: [
    NgIf,
    NgFor,
    SharedModule,
    RouterLinkWithHref,
    MonitoringApproachTypePipe,
    ReturnToLinkComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationReviewDecisionGroupComponent,
    MonitoringApproachSummaryTemplateComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringSummaryComponent extends BaseMonitoringApproachComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('emissionsMonitoringApproach')),
    this.store.pipe(empQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => {
      return {
        emissionsMonitoringApproach: getSubtaskSummaryValues(this.formProvider.form),
        files:
          this.formProvider.simplifiedApproachForm?.getRawValue()?.supportingEvidenceFiles?.map((doc) => {
            return {
              fileName: doc.file.name,
              downloadUrl: `${this.store.empUkEtsDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
            };
          }) ?? [],
        pageHeader: getSummaryHeaderForTaskType(type, 'emissionsMonitoringApproach'),
        isEditable,
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          ['EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT'].includes(type),
        showDecision: showReviewDecisionComponent.includes(type),
        showVariationDecision: showVariationReviewDecisionComponent.includes(type),
        showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
        showDiff: !!originalEmpContainer,
        originalemissionsMonitoringApproach: {
          simplifiedApproach: originalEmpContainer?.emissionsMonitoringPlan.emissionsMonitoringApproach as
            | SmallEmittersMonitoringApproach
            | SupportFacilityMonitoringApproach,
          monitoringApproachType:
            originalEmpContainer?.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType,
        },
        originalFiles:
          (
            originalEmpContainer?.emissionsMonitoringPlan.emissionsMonitoringApproach as
              | SmallEmittersMonitoringApproach
              | SupportFacilityMonitoringApproach
          )?.supportingEvidenceFiles?.map((doc) => {
            return {
              fileName: originalEmpContainer?.empAttachments[doc],
              downloadUrl: `${this.store.empUkEtsDelegate.baseFileAttachmentDownloadUrl}/${doc}`,
            };
          }) ?? [],
      };
    }),
  );

  onSubmit() {
    if (this.form?.valid) {
      this.saveEmpAndNavigate(this.formProvider.getFormValue(), 'complete', '../../../../');
    }
  }
}
