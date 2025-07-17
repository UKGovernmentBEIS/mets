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
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSubtaskSummaryValues,
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { MethodBProceduresSummaryTemplateComponent } from '@aviation/shared/components/emp/method-b-procedures-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmpMethodBProcedures } from 'pmrv-api';

import { empQuery } from '../../emp.selectors';
import { EmpReviewDecisionGroupComponent } from '../../emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '../../emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { MethodBProceduresFormProvider } from '../method-b-procedures-form.provider';

interface ViewModel {
  data: EmpMethodBProcedures;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalData: EmpMethodBProcedures;
}

@Component({
  selector: 'app-method-b-procedures-summary',
  templateUrl: './method-b-procedures-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    MethodBProceduresSummaryTemplateComponent,
    EmpVariationReviewDecisionGroupComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MethodBProceduresSummaryComponent {
  private store = inject(RequestTaskStore);
  private formProvider = inject<MethodBProceduresFormProvider>(TASK_FORM_PROVIDER);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);

  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('methodBProcedures')),
    this.store.pipe(empQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => ({
      data: getSubtaskSummaryValues(this.formProvider.form),
      pageHeader: getSummaryHeaderForTaskType(type, 'methodBProcedures'),
      isEditable,
      hideSubmit:
        !isEditable ||
        ['complete', 'cannot start yet'].includes(taskStatus) ||
        variationSubmitRegulatorLedRequestTaskTypes.includes(type) ||
        variationOperatorLedReviewRequestTaskTypes.includes(type) ||
        issuanceReviewRequestTaskTypes.includes(type),
      showDecision: showReviewDecisionComponent.includes(type),
      showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
      showDiff: !!originalEmpContainer,
      originalData: originalEmpContainer?.emissionsMonitoringPlan?.methodBProcedures,
    })),
  );

  onSubmit() {
    this.store.empDelegate
      .saveEmp({ methodBProcedures: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => this.router.navigate(['../../../..'], { relativeTo: this.route }));
  }
}
