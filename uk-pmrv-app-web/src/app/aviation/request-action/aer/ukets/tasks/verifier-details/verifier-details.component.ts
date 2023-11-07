import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import AerVerifierDetailsGroupFormComponent from '@aviation/request-task/aer/ukets/aer-verify/tasks/verifier-details/verifier-details-group-form/verifier-details-group-form.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUkEtsVerificationReport, RequestActionDTO } from 'pmrv-api';

import { aerQuery } from '../../aer-ukets.selectors';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  verificationReportData: AviationAerUkEtsVerificationReport;
}

@Component({
  selector: 'app-verifier-details',
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-aer-verifier-details-group-form
        [verificationReport]="vm.verificationReportData"
      ></app-aer-verifier-details-group-form>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, AerVerifierDetailsGroupFormComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VerifierDetailsComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['verificationReport'],
        verificationReportData: {
          ...payload.verificationReport,
          verificationBodyDetails: payload.verificationReport.verificationBodyDetails,
          verificationTeamDetails: payload.verificationReport.verificationTeamDetails,
          verifierContact: payload.verificationReport.verifierContact,
        },
      };
    }),
  );
}
