import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { EmpReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { variationSubmitRegulatorLedRequestTaskTypes } from '@aviation/request-task/emp/shared/util/emp.util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmpAbbreviations } from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import {
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '../../../../util';
import { empQuery } from '../../emp.selectors';
import { AbbreviationsFormProvider } from '../abbreviations-form.provider';

interface ViewModel {
  data: EmpAbbreviations;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalData: EmpAbbreviations;
}

@Component({
  selector: 'app-abbreviations-summary',
  templateUrl: './abbreviations-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    EmpVariationReviewDecisionGroupComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AbbreviationsSummaryComponent {
  private store = inject(RequestTaskStore);
  private formProvider = inject<AbbreviationsFormProvider>(TASK_FORM_PROVIDER);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('abbreviations')),
    this.store.pipe(empQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => ({
      data: this.formProvider.getFormValue(),
      pageHeader: getSummaryHeaderForTaskType(type, 'abbreviations'),
      isEditable,
      hideSubmit:
        !isEditable ||
        ['complete', 'cannot start yet'].includes(taskStatus) ||
        variationSubmitRegulatorLedRequestTaskTypes.includes(type),
      showDecision: showReviewDecisionComponent.includes(type),
      showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
      showDiff: !!originalEmpContainer,
      originalData: originalEmpContainer?.emissionsMonitoringPlan?.abbreviations,
    })),
  );

  onSubmit() {
    this.store.empDelegate
      .saveEmp({ abbreviations: this.form.value }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => this.router.navigate(['../../../..'], { relativeTo: this.route }));
  }
}
