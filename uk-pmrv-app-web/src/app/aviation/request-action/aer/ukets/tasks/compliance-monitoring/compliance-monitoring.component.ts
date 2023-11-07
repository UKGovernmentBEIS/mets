import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { ComplianceMonitoringGroupComponent } from '@aviation/request-task/aer/ukets/aer-verify/tasks/compliance-monitoring/compliance-monitoring-group/compliance-monitoring-group.component';
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
      [breadcrumb]="true"
    >
      <app-compliance-monitoring-group-template
        [compliance]="vm.complianceMonitoringReportingRules"
      ></app-compliance-monitoring-group-template>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, ComplianceMonitoringGroupComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ComplianceMonitoringComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['complianceMonitoringReportingRules'],
        complianceMonitoringReportingRules: {
          ...payload.verificationReport.complianceMonitoringReportingRules,
        },
      };
    }),
  );
}
