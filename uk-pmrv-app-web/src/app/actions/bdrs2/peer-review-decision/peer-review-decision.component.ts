import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { CommonActionsStore } from '@actions/store/common-actions.store';

import { PeerReviewDecisionSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { Bdrs2ActionService } from '../core/bdrs2.service';

interface ViewModel {
  actionType: RequestActionDTO['type'];
  decision: PeerReviewDecisionSubmittedRequestActionPayload['decision'];
  submitter: RequestActionDTO['submitter'];
}

@Component({
  selector: 'app-action-bdrs2-peer-review-decision',
  imports: [ActionSharedModule],
  template: `
    @if (vm(); as vm) {
      <app-peer-review-decision-template
        [requestActionType]="vm.actionType"
        [decision]="vm.decision"
        [submitter]="vm.submitter"></app-peer-review-decision-template>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2PeerReviewDecisionComponent {
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

  private readonly payload = this.bdrs2ActionService.payload as Signal<PeerReviewDecisionSubmittedRequestActionPayload>;
  private readonly submitter = toSignal(this.commonActionsStore.requestAction$.pipe(map((a) => a.submitter)));
  private readonly requestActionType = this.bdrs2ActionService.requestActionType;

  constructor(
    private readonly bdrs2ActionService: Bdrs2ActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
