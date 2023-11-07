import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { NonComplianceNoticeOfIntentRequestTaskPayload } from 'pmrv-api';

import { NonComplianceService } from '../core/non-compliance.service';
import { resolveSectionStatus } from './section.status';

@Component({
  selector: 'app-notice-of-intent',
  templateUrl: './notice-of-intent.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NoticeOfIntentComponent {
  sectionStatus$ = this.nonComplianceService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as NonComplianceNoticeOfIntentRequestTaskPayload)),
  );

  allowNotifyOperator$ = this.nonComplianceService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        resolveSectionStatus(data.requestTask.payload as NonComplianceNoticeOfIntentRequestTaskPayload) ===
          'complete' && data.allowedRequestTaskActions.includes('NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR'),
    ),
  );

  allowSendPeerReview$ = this.nonComplianceService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        resolveSectionStatus(data.requestTask.payload as NonComplianceNoticeOfIntentRequestTaskPayload) ===
          'complete' && data.allowedRequestTaskActions.includes('NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW'),
    ),
  );

  constructor(
    private readonly nonComplianceService: NonComplianceService,
    readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }
}
