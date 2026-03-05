import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { ComplianceMonitoringGroupComponent } from '@aviation/request-task/aer/ukets/aer-verify/tasks/compliance-monitoring/compliance-monitoring-group/compliance-monitoring-group.component';
import { AerVerificationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-verification-review-decision-group-summary/aer-verification-review-decision-group-summary.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerComplianceMonitoringReportingRules, RequestActionDTO } from 'pmrv-api';

import { aerQuery } from '../../aer-ukets.selectors';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  complianceMonitoringReportingRules: AviationAerComplianceMonitoringReportingRules;
}

@Component({
  selector: 'app-compliance-monitoring',
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-compliance-monitoring-group-template
        [compliance]="vm.complianceMonitoringReportingRules"></app-compliance-monitoring-group-template>

      <ng-container *ngIf="vm.showDecision">
        <h2 app-summary-header class="govuk-heading-m">Decision Summary</h2>
        <app-aer-verification-review-decision-group-summary
          [data]="vm.reviewVerifyDecision"></app-aer-verification-review-decision-group-summary>
      </ng-container>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    ComplianceMonitoringGroupComponent,
    AerVerificationReviewDecisionGroupSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ComplianceMonitoringComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['complianceMonitoringReportingRules'],
        complianceMonitoringReportingRules: {
          ...payload.verificationReport.complianceMonitoringReportingRules,
        },
        ...getAerDecisionReview(
          payload,
          requestActionType,
          regulatorViewer,
          'complianceMonitoringReportingRules',
          false,
        ),
      };
    }),
  );
}
