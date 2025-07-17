import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { CorsiaRequestTypes } from '@aviation/request-task/util';
import { AerVerificationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-verification-review-decision-group-summary/aer-verification-review-decision-group-summary.component';
import { OverallDecisionGroupComponent } from '@aviation/shared/components/aer-verify/overall-decision-group';
import { SharedModule } from '@shared/shared.module';

import { AviationAerVerificationDecision, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  overallDecision: AviationAerVerificationDecision;
  isCorsia: boolean;
}

@Component({
  selector: 'app-overall-decision',
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-aviation-overall-decision-group
        [isCorsia]="vm.isCorsia"
        [overallAssessment]="vm.overallDecision"></app-aviation-overall-decision-group>

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
    OverallDecisionGroupComponent,
    AerVerificationReviewDecisionGroupSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OverallDecisionComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRequestType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, requestType, regulatorViewer]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['overallDecision'],
        overallDecision: {
          ...payload.verificationReport.overallDecision,
        },
        isCorsia: CorsiaRequestTypes.includes(requestType),
        ...getAerDecisionReview(
          payload,
          requestActionType,
          regulatorViewer,
          CorsiaRequestTypes.includes(requestType) ? 'VERIFICATION_STATEMENT_CONCLUSIONS' : 'overallDecision',
          false,
        ),
      };
    }),
  );
}
