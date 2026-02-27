import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { SharedModule } from '@shared/shared.module';

import {
  ALRApplicationAcceptedRequestActionPayload,
  ALRApplicationAcceptedWithCorrectionsRequestActionPayload,
  ALRApplicationAuthorityReviewOutcome,
  ALRApplicationRejectedRequestActionPayload,
  DecisionNotification,
  RequestActionDTO,
} from 'pmrv-api';

import { AlrActionService } from '../core/alr.service';

interface ViewModel {
  actionType: RequestActionDTO['type'];
  decisionNotification: DecisionNotification;
  authorityResponse: ALRApplicationAuthorityReviewOutcome['authorityResponse'];
}

@Component({
  selector: 'app-alr-action-completed',
  imports: [ActionSharedModule, SharedModule, NgIf, RouterLink],
  templateUrl: './completed.component.html',
  providers: [UserInfoResolverPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrActionCompletedComponent {
  vm: Signal<ViewModel> = computed(() => {
    const actionType = this.actionType();
    const payload = this.payload();

    return {
      actionType,
      authorityResponse: payload.authorityReviewOutcome.authorityResponse,
      decisionNotification: payload.decisionNotification,
    };
  });

  private readonly actionType = this.alrActionService.requestActionType;
  private readonly payload = this.alrActionService.payload as
    | Signal<ALRApplicationAcceptedRequestActionPayload>
    | Signal<ALRApplicationAcceptedWithCorrectionsRequestActionPayload>
    | Signal<ALRApplicationRejectedRequestActionPayload>;

  constructor(private readonly alrActionService: AlrActionService) {}
}
