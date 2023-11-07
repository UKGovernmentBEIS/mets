import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyCorsiaHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { VerifierDetailsCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/verifier-details-corsia-template/verifier-details-corsia-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaVerifierDetails, RequestActionDTO, VerificationBodyDetails } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  verificationBodyDetails: VerificationBodyDetails;
  verifierDetails: AviationAerCorsiaVerifierDetails;
}

@Component({
  selector: 'app-verifier-details',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, VerifierDetailsCorsiaTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-verifier-details-corsia-template
        [verificationBodyDetails]="vm.verificationBodyDetails"
        [verifierDetails]="vm.verifierDetails"
      ></app-verifier-details-corsia-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VerifierDetailsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectVerificationReport),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([verificationReport, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerVerifyCorsiaHeaderTaskMap['verifierDetails'],
      verificationBodyDetails: verificationReport.verificationBodyDetails,
      verifierDetails: verificationReport.verifierDetails,
    })),
  );
}
