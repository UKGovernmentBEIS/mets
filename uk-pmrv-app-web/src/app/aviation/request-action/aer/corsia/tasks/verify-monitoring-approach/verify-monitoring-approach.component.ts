import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyCorsiaHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
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
  imports: [SharedModule, RequestActionTaskComponent, MonitoringApproachVerifyCorsiaTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-monitoring-approach-verify-corsia-template
        [opinionStatement]="vm.opinionStatement"
        [totalEmissionsProvided]="vm.totalEmissionsProvided"
        [totalOffsetEmissionsProvided]="vm.totalOffsetEmissionsProvided"
      ></app-monitoring-approach-verify-corsia-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VerifyMonitoringApproachComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectRequestActionPayload),
    this.store.pipe(aerCorsiaQuery.selectVerificationReport),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, verificationReport, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerVerifyCorsiaHeaderTaskMap['opinionStatement'],
      opinionStatement: verificationReport.opinionStatement,
      totalEmissionsProvided: payload.totalEmissionsProvided,
      totalOffsetEmissionsProvided: payload.totalOffsetEmissionsProvided,
    })),
  );
}
