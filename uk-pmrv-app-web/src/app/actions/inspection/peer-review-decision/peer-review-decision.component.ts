import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { RequestActionDTO } from 'pmrv-api';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { CommonActionsStore } from '../../store/common-actions.store';
import { InspectionActionService } from '../core/inspection-action.service';

@Component({
  selector: 'app-action-inspection-peer-review-decision',
  standalone: true,
  imports: [SharedModule, ActionSharedModule],
  templateUrl: './peer-review-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewDecisionComponent {
  expectedActionType: RequestActionDTO['type'][] = [
    'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_ACCEPTED',
    'INSTALLATION_AUDIT_APPLICATION_PEER_REVIEWER_ACCEPTED',
    'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_REJECTED',
    'INSTALLATION_AUDIT_APPLICATION_PEER_REVIEWER_REJECTED',
  ];
  actionType$ = this.commonActionsStore.requestActionType$;
  decision$ = this.inspectionActionService.getPeerReviewPayload$().pipe(map((payload) => payload.decision));
  submitter$ = this.commonActionsStore.requestAction$.pipe(map((a) => a.submitter));

  constructor(
    private readonly inspectionActionService: InspectionActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
