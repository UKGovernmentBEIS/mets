/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
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
      [breadcrumb]="true"
    >
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
        *ngIf="vm.uncorrectedMisstatements?.exist"
      ></app-uncorrected-item-group>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, UncorrectedItemGroupComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedMisstatementsComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['uncorrectedMisstatements'],
        uncorrectedMisstatements: {
          ...payload.verificationReport.uncorrectedMisstatements,
        },
      };
    }),
  );
}
