import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { ApplicationTimeframeSummaryTemplateComponent } from '@aviation/shared/components/emp/application-timeframe-summary-template/application-timeframe-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpApplicationTimeframeInfo } from 'pmrv-api';

import { empQuery } from '../../../../shared/emp.selectors';
import { EmpReviewDecisionGroupComponent } from '../../../../shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '../../../../shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { ApplicationTimeframeFormProvider } from '../application-timeframe-form.provider';

interface ViewModel {
  data: EmpApplicationTimeframeInfo;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalData: EmpApplicationTimeframeInfo;
}
@Component({
  selector: 'app-application-timeframe-summary',
  templateUrl: './application-timeframe-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgIf,
    ReturnToLinkComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationReviewDecisionGroupComponent,
    ApplicationTimeframeSummaryTemplateComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApplicationTimeframeSummaryComponent {
  private form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('applicationTimeframeInfo')),
    this.store.pipe(empQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => {
      return {
        data: this.form.value,
        pageHeader: getSummaryHeaderForTaskType(type, 'applicationTimeframeInfo'),
        isEditable,
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          ['EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT'].includes(type),
        showDecision: showReviewDecisionComponent.includes(type),
        showVariationDecision: showVariationReviewDecisionComponent.includes(type),
        showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
        showDiff: !!originalEmpContainer,
        originalData: originalEmpContainer?.emissionsMonitoringPlan?.applicationTimeframeInfo,
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: ApplicationTimeframeFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (this.form?.valid) {
      this.store.empUkEtsDelegate
        .saveEmp({ applicationTimeframeInfo: this.form.value }, 'complete')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../../../'], { relativeTo: this.route, queryParams: { change: null } });
        });
    }
  }
}
