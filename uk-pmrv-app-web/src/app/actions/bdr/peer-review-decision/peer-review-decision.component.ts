import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { CommonActionsStore } from '@actions/store/common-actions.store';

import { PeerReviewDecisionSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { BdrActionService } from '../core/bdr.service';

interface ViewModel {
  actionType: RequestActionDTO['type'];
  decision: PeerReviewDecisionSubmittedRequestActionPayload['decision'];
  submitter: RequestActionDTO['submitter'];
}

@Component({
  selector: 'app-action-bdr-peer-review-decision',
  imports: [ActionSharedModule, NgIf],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-peer-review-decision-template
        [requestActionType]="vm.actionType"
        [decision]="vm.decision"
        [submitter]="vm.submitter"></app-peer-review-decision-template>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrPeerReviewDecisionComponent {
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

  private readonly payload = this.bdrActionService.payload as Signal<PeerReviewDecisionSubmittedRequestActionPayload>;
  private readonly submitter = toSignal(this.commonActionsStore.requestAction$.pipe(map((a) => a.submitter)));
  private readonly requestActionType = this.bdrActionService.requestActionType;

  constructor(
    private readonly bdrActionService: BdrActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
