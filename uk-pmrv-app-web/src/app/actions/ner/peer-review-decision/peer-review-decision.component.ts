import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { CommonActionsStore } from '@actions/store/common-actions.store';

import { PeerReviewDecisionSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { NerActionService } from '../core';

interface ViewModel {
  actionType: RequestActionDTO['type'];
  decision: PeerReviewDecisionSubmittedRequestActionPayload['decision'];
  submitter: RequestActionDTO['submitter'];
}

@Component({
  selector: 'app-action-ner-peer-review-decision',
  imports: [ActionSharedModule],
  template: `
    @let vm = this.vm();

    <app-peer-review-decision-template
      [requestActionType]="vm.actionType"
      [decision]="vm.decision"
      [submitter]="vm.submitter"></app-peer-review-decision-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerPeerReviewDecisionComponent {
  private readonly nerActionService = inject(NerActionService);
  private readonly commonActionsStore = inject(CommonActionsStore);
  private readonly payload = this.nerActionService.payload as Signal<PeerReviewDecisionSubmittedRequestActionPayload>;
  private readonly submitter = toSignal(this.commonActionsStore.requestAction$.pipe(map((a) => a.submitter)));
  private readonly requestActionType = this.nerActionService.requestActionType;

  vm: Signal<ViewModel> = computed(() => {
    const requestActionType = this.requestActionType();
    const decision = this.payload().decision;
    const submitter = this.submitter();

    return {
      actionType: requestActionType,
      decision,
      submitter,
    };
  });
}
