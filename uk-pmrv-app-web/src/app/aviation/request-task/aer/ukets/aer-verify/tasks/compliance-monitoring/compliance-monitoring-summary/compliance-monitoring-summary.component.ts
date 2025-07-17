import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, tap } from 'rxjs';

import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUkEtsVerificationReport } from 'pmrv-api';

import { aerVerifyQuery } from '../../../aer-verify.selector';
import { ComplianceMonitoringFormProvider } from '../compliance-monitoring-form.provider';
import { ComplianceMonitoringGroupComponent } from '../compliance-monitoring-group/compliance-monitoring-group.component';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  verificationReportData: AviationAerUkEtsVerificationReport;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-compliance-monitoring-summary',
  templateUrl: './compliance-monitoring-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    ComplianceMonitoringGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ComplianceMonitoringSummaryComponent {
  private verificationReport: AviationAerUkEtsVerificationReport;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: ComplianceMonitoringFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyQuery.selectVerificationReport),
    this.store.pipe(aerVerifyQuery.selectStatusForTask('complianceMonitoringReportingRules')),
  ]).pipe(
    map(([type, isEditable, verificationReportData, taskStatus]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'complianceMonitoringReportingRules'),
        isEditable,
        verificationReportData,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
    tap((data) => {
      this.verificationReport = data.verificationReportData;
    }),
  );

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ complianceMonitoringReportingRules: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setComplianceMonitoring(
          this.formProvider.getFormValue(),
        );
        this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }
}
