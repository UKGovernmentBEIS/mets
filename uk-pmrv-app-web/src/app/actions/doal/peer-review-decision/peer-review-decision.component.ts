import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { CommonActionsStore } from '../../store/common-actions.store';
import { DoalActionService } from '../core/doal-action.service';

@Component({
  selector: 'app-doal-peer-review-decision',
  templateUrl: './peer-review-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewDecisionComponent {
  actionType$ = this.commonActionsStore.requestActionType$;
  decision$ = this.doalActionService.getPeerReviewPayload$().pipe(map((payload) => payload.decision));
  submitter$ = this.commonActionsStore.requestAction$.pipe(map((a) => a.submitter));

  constructor(
    private readonly doalActionService: DoalActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
