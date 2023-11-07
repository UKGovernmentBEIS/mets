import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyCorsiaHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
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
  imports: [SharedModule, RequestActionTaskComponent, ProcessAnalysisCorsiaTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-process-analysis-corsia-template [data]="vm.data"></app-process-analysis-corsia-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ProcessAnalysisComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectVerificationReport),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([verificationReport, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerVerifyCorsiaHeaderTaskMap['processAnalysis'],
      data: verificationReport.processAnalysis,
    })),
  );
}
