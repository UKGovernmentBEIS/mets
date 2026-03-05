import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { PermitNotificationService } from '../core/permit-notification.service';

interface ViewModel {
  pageTitle: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  reviewDecision: any | PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload['reviewDecision'];
  permitNotification:
    | any
    | PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload['permitNotification'];
  supportingDocuments: Array<AttachedFile>;
}

@Component({
  selector: 'app-action-notification-cessation-completed',
  standalone: true,
  imports: [SharedModule, ActionSharedModule],
  templateUrl: './cessation-completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserInfoResolverPipe],
})
export class NotificationCessationCompletedActionComponent {
  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();

    return {
      pageTitle: this.routeData()?.pageTitle,
      expectedActionType: ['PERMIT_NOTIFICATION_APPLICATION_CESSATION_COMPLETED'],
      reviewDecision: payload.reviewDecision,
      permitNotification: payload.permitNotification,
      supportingDocuments: this.permitNotificationService.getDownloadUrlFiles(payload.permitNotification.documents),
    };
  });

  private readonly routeData = toSignal(this.route.data);
  private readonly payload: Signal<PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload> = toSignal(
    this.permitNotificationService.getPayload(),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly permitNotificationService: PermitNotificationService,
  ) {}
}
