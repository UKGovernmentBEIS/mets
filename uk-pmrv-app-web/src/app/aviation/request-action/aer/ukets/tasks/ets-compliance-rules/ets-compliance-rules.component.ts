import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { SharedModule } from '@shared/shared.module';

import { AviationAerEtsComplianceRules, RequestActionDTO } from 'pmrv-api';

import { aerQuery } from '../../aer-ukets.selectors';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  etsComplianceRules: AviationAerEtsComplianceRules;
}

@Component({
  selector: 'app-ets-compliance-rules',
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <h2 class="govuk-heading-m">Details</h2>

      <app-ets-compliance-rules-group [etsComplianceRules]="vm.etsComplianceRules"></app-ets-compliance-rules-group>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class EtsComplianceRulesComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['etsComplianceRules'],
        etsComplianceRules: {
          ...payload.verificationReport.etsComplianceRules,
        },
      };
    }),
  );
}
