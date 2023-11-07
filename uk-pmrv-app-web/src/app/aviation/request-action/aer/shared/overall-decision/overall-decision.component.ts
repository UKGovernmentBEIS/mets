import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { CorsiaRequestTypes } from '@aviation/request-task/util';
import { OverallDecisionGroupComponent } from '@aviation/shared/components/aer-verify/overall-decision-group';
import { SharedModule } from '@shared/shared.module';

import { AviationAerVerificationDecision, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  overallDecision: AviationAerVerificationDecision;
  isCorsia: boolean;
}

@Component({
  selector: 'app-overall-decision',
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <h2 class="govuk-heading-m">Details</h2>

      <app-aviation-overall-decision-group
        [isCorsia]="vm.isCorsia"
        [overallAssessment]="vm.overallDecision"
      ></app-aviation-overall-decision-group>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, OverallDecisionGroupComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OverallDecisionComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRequestType),
  ]).pipe(
    map(([payload, requestActionType, requestType]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['overallDecision'],
        overallDecision: {
          ...payload.verificationReport.overallDecision,
        },
        isCorsia: CorsiaRequestTypes.includes(requestType),
      };
    }),
  );
}
