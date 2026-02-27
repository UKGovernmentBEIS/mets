import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { SharedModule } from '@shared/shared.module';

import { PeerReviewDecisionSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { HseTiActionService } from '../core/hseti.service';

interface ViewModel {
  actionType: RequestActionDTO['type'];
  expectedActionType: Array<RequestActionDTO['type']>;
  decision: PeerReviewDecisionSubmittedRequestActionPayload['decision'];
  submitter: RequestActionDTO['submitter'];
}

@Component({
  selector: 'app-action-hseti-peer-review-decision',
  imports: [SharedModule, ActionSharedModule],
  templateUrl: './peer-review-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HsetiPeerReviewDecisionComponent {
  vm: Signal<ViewModel> = computed(() => {
    const requestActionType = this.requestActionType();
    const decision = this.payload().decision;
    const submitter = this.submitter();

    return {
      actionType: requestActionType,
      expectedActionType: [requestActionType],
      decision,
      submitter,
    };
  });

  private readonly payload = this.hsetiActionService.payload as Signal<PeerReviewDecisionSubmittedRequestActionPayload>;
  private readonly submitter = toSignal(this.commonActionsStore.requestAction$.pipe(map((a) => a.submitter)));
  private readonly requestActionType = this.hsetiActionService.requestActionType;

  constructor(
    private readonly hsetiActionService: HseTiActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
