import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitNotificationService } from '@tasks/permit-notification/core/permit-notification.service';
import { getPreviewDocumentsInfo } from '@tasks/permit-notification/utils/previewDocumentsNotification.util';

import { CessationNotification, PermitNotificationApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';

@Component({
  selector: 'app-peer-review',
  templateUrl: './peer-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewComponent {
  readonly daysRemaining$ = this.store.pipe(
    first(),
    map((state) => state.requestTaskItem.requestTask.daysRemaining),
  );

  readonly permitNotification: Signal<CessationNotification> = toSignal(
    this.permitNotificationService.permitNotification$,
  );
  readonly permitNotificationTitle: Signal<string> = computed(() =>
    this.permitNotification()?.type === 'CESSATION'
      ? 'Peer review permanent cessation'
      : 'Notification permit peer review',
  );

  readonly determinationStatus$ = this.store.pipe(
    map(
      (state) =>
        (state?.requestTaskItem?.requestTask?.payload as PermitNotificationApplicationReviewRequestTaskPayload)
          ?.reviewDecision?.type,
    ),
  );

  previewDocuments$ = this.determinationStatus$.pipe(
    map((determinationStatus) => {
      return getPreviewDocumentsInfo('PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION', determinationStatus);
    }),
  );

  readonly allowPeerReviewDecision$: Observable<boolean> = this.store.pipe(
    first(),
    map((state) => {
      return state.requestTaskItem.allowedRequestTaskActions?.includes(
        'PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION',
      );
    }),
  );

  peerReviewDecision(): void {
    this.router.navigate(['./decision'], { relativeTo: this.route });
  }

  constructor(
    readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly permitNotificationService: PermitNotificationService,
  ) {}
}
