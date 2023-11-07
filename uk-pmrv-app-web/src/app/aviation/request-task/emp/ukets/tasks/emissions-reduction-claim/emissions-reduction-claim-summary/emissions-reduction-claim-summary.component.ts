import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { EmissionsReductionClaimSummaryTemplateComponent } from '@aviation/shared/components/emp/emissions-reduction-claim-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmpEmissionsReductionClaim } from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import {
  getSubtaskSummaryValues,
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '../../../../../util';
import { empQuery } from '../../../../shared/emp.selectors';
import { EmpReviewDecisionGroupComponent } from '../../../../shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '../../../../shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  emissionsReduction: EmpEmissionsReductionClaim;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalEmissionsReduction: EmpEmissionsReductionClaim;
}

@Component({
  selector: 'app-emissions-reduction-claim-summary',
  standalone: true,
  imports: [
    SharedModule,
    EmissionsReductionClaimSummaryTemplateComponent,
    ReturnToLinkComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationReviewDecisionGroupComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  templateUrl: './emissions-reduction-claim-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimSummaryComponent {
  private store = inject(RequestTaskStore);
  private formProvider = inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('emissionsReductionClaim')),
    this.store.pipe(empQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => ({
      pageHeader: getSummaryHeaderForTaskType(type, 'emissionsReductionClaim'),
      isEditable,
      emissionsReduction: getSubtaskSummaryValues(this.formProvider.form),
      hideSubmit:
        !isEditable ||
        ['complete', 'cannot start yet'].includes(taskStatus) ||
        ['EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT'].includes(type),
      showDecision: showReviewDecisionComponent.includes(type),
      showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
      showDiff: !!originalEmpContainer,
      originalEmissionsReduction: originalEmpContainer?.emissionsMonitoringPlan?.emissionsReductionClaim,
    })),
  );

  onSubmit() {
    this.store.empUkEtsDelegate
      .saveEmp({ emissionsReductionClaim: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => this.router.navigate(['../../../..'], { relativeTo: this.route }));
  }
}
