/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { VerifierCommentGroupComponent } from '@aviation/shared/components/aer-verify/verifier-comment-group/verifier-comment-group.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUncorrectedNonConformities, RequestActionDTO } from 'pmrv-api';

import { aerQuery } from '../../aer-ukets.selectors';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  uncorrectedNonConformities: AviationAerUncorrectedNonConformities;
}

@Component({
  selector: 'app-uncorrected-non-conformities',
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <h2 class="govuk-heading-m">Non-conformities with the approved monitoring plan</h2>

      <dl govuk-summary-list [hasBorders]="true">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>
            Have there been any uncorrected non-conformities with the approved emissions monitoring plan?
          </dt>
          <dd govukSummaryListRowValue>
            {{ vm.uncorrectedNonConformities?.existUncorrectedNonConformities ? 'Yes' : 'No' }}
          </dd>
        </div>
      </dl>

      <app-uncorrected-item-group
        [uncorrectedItems]="vm.uncorrectedNonConformities.uncorrectedNonConformities"
        *ngIf="vm.uncorrectedNonConformities.existUncorrectedNonConformities"
      ></app-uncorrected-item-group>

      <h2 class="govuk-heading-m">Non-conformities from the previous year that have not been resolved</h2>

      <dl govuk-summary-list [hasBorders]="true">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>
            Are there any non-conformities from the previous year that have not been resolved?
          </dt>
          <dd govukSummaryListRowValue>{{ vm.uncorrectedNonConformities?.existPriorYearIssues ? 'Yes' : 'No' }}</dd>
        </div>
      </dl>

      <app-verifier-comment-group
        [verifierComments]="vm.uncorrectedNonConformities.priorYearIssues"
        *ngIf="vm.uncorrectedNonConformities.existPriorYearIssues"
      ></app-verifier-comment-group>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, UncorrectedItemGroupComponent, VerifierCommentGroupComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedNonConformitiesComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['uncorrectedNonConformities'],
        uncorrectedNonConformities: {
          ...payload.verificationReport.uncorrectedNonConformities,
        },
      };
    }),
  );
}
