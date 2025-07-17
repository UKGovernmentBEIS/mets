import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyCorsiaHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { AerVerificationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-verification-review-decision-group-summary/aer-verification-review-decision-group-summary.component';
import { MonitoringApproachVerifyCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/monitoring-approach-verify-corsia-template/monitoring-approach-verify-corsia-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaOpinionStatement, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  opinionStatement: AviationAerCorsiaOpinionStatement;
  totalEmissionsProvided: string;
  totalOffsetEmissionsProvided: string;
}

@Component({
  selector: 'app-verify-monitoring-approach',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    MonitoringApproachVerifyCorsiaTemplateComponent,
    AerVerificationReviewDecisionGroupSummaryComponent,
  ],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-monitoring-approach-verify-corsia-template
        [opinionStatement]="vm.opinionStatement"
        [totalEmissionsProvided]="vm.totalEmissionsProvided"
        [totalOffsetEmissionsProvided]="
          vm.totalOffsetEmissionsProvided
        "></app-monitoring-approach-verify-corsia-template>

      <ng-container *ngIf="vm.showDecision">
        <h2 app-summary-header class="govuk-heading-m">Decision Summary</h2>
        <app-aer-verification-review-decision-group-summary
          [data]="vm.reviewVerifyDecision"></app-aer-verification-review-decision-group-summary>
      </ng-container>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VerifyMonitoringApproachComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectRequestActionPayload),
    this.store.pipe(aerCorsiaQuery.selectVerificationReport),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, verificationReport, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerVerifyCorsiaHeaderTaskMap['opinionStatement'],
      opinionStatement: verificationReport.opinionStatement,
      totalEmissionsProvided:
        requestActionType === 'AVIATION_AER_CORSIA_APPLICATION_COMPLETED'
          ? payload?.submittedEmissions?.totalEmissions?.allFlightsEmissions
          : payload.totalEmissionsProvided,
      totalOffsetEmissionsProvided:
        requestActionType === 'AVIATION_AER_CORSIA_APPLICATION_COMPLETED'
          ? payload?.submittedEmissions?.totalEmissions?.offsetFlightsEmissions
          : payload.totalOffsetEmissionsProvided,
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'MONITORING_APPROACH_EMISSIONS', false),
    })),
  );
}
