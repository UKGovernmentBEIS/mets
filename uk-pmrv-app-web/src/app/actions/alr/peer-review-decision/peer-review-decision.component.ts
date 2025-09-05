import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { CommonActionsStore } from '@actions/store/common-actions.store';

import { PeerReviewDecisionSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { AlrActionService } from '../core/alr.service';

interface ViewModel {
  actionType: RequestActionDTO['type'];
  decision: PeerReviewDecisionSubmittedRequestActionPayload['decision'];
  submitter: RequestActionDTO['submitter'];
}

@Component({
  selector: 'app-alr-action-peer-review-decision',
  standalone: true,
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
export class AlrPeerReviewDecisionComponent {
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

  private readonly payload = this.alrActionService.payload as Signal<PeerReviewDecisionSubmittedRequestActionPayload>;
  private readonly submitter = toSignal(this.commonActionsStore.requestAction$.pipe(map((a) => a.submitter)));
  private readonly requestActionType = this.alrActionService.requestActionType;

  constructor(
    private readonly alrActionService: AlrActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
