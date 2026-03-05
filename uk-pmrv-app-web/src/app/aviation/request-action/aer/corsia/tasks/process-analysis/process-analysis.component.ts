import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyCorsiaHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { AerVerificationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-verification-review-decision-group-summary/aer-verification-review-decision-group-summary.component';
import { ProcessAnalysisCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/process-analysis-corsia-template/process-analysis-corsia-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaProcessAnalysis, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: AviationAerCorsiaProcessAnalysis;
}

@Component({
  selector: 'app-process-analysis',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    ProcessAnalysisCorsiaTemplateComponent,
    AerVerificationReviewDecisionGroupSummaryComponent,
  ],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-process-analysis-corsia-template [data]="vm.data"></app-process-analysis-corsia-template>

      <ng-container *ngIf="vm.showDecision">
        <h2 app-summary-header class="govuk-heading-m">Decision Summary</h2>
        <app-aer-verification-review-decision-group-summary
          [data]="vm.reviewVerifyDecision"></app-aer-verification-review-decision-group-summary>
      </ng-container>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ProcessAnalysisComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectRequestActionPayload),
    this.store.pipe(aerCorsiaQuery.selectVerificationReport),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, verificationReport, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerVerifyCorsiaHeaderTaskMap['processAnalysis'],
      data: verificationReport.processAnalysis,
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'processAnalysis', false),
    })),
  );
}
