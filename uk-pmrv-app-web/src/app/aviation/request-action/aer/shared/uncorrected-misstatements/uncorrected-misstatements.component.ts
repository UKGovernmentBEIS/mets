/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { AerVerificationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-verification-review-decision-group-summary/aer-verification-review-decision-group-summary.component';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUncorrectedMisstatements, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  uncorrectedMisstatements: AviationAerUncorrectedMisstatements;
}

@Component({
  selector: 'app-uncorrected-misstatements',
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <h2 class="govuk-heading-m">Misstatements not corrected before issuing this report</h2>

      <dl govuk-summary-list [hasBorders]="true">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>
            Are there any misstatements that were not corrected before issuing this report?
          </dt>
          <dd govukSummaryListRowValue>{{ vm.uncorrectedMisstatements?.exist ? 'Yes' : 'No' }}</dd>
        </div>
      </dl>

      <app-uncorrected-item-group
        [uncorrectedItems]="vm.uncorrectedMisstatements.uncorrectedMisstatements"
        *ngIf="vm.uncorrectedMisstatements?.exist"></app-uncorrected-item-group>

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
    UncorrectedItemGroupComponent,
    AerVerificationReviewDecisionGroupSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedMisstatementsComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['uncorrectedMisstatements'],
        uncorrectedMisstatements: {
          ...payload.verificationReport.uncorrectedMisstatements,
        },
        ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'uncorrectedMisstatements', false),
      };
    }),
  );
}
