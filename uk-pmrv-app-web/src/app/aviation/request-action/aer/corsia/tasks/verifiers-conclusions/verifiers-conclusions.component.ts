import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyCorsiaHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { VerifiersConclusionsCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/verifiers-conclusions-corsia-template/verifiers-conclusions-corsia-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaVerifiersConclusions, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: AviationAerCorsiaVerifiersConclusions;
}

@Component({
  selector: 'app-verifiers-conclusions',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, RouterLink, VerifiersConclusionsCorsiaTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-verifiers-conclusions-corsia-template [data]="vm.data"></app-verifiers-conclusions-corsia-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VerifiersConclusionsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectVerificationReport),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([verificationReport, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerVerifyCorsiaHeaderTaskMap['verifiersConclusions'],
      data: verificationReport.verifiersConclusions,
    })),
  );
}
