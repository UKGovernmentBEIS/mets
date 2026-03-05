import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { NonComplianceDailyPenaltyNoticeRequestTaskPayload } from 'pmrv-api';

import { BreadcrumbService } from '../../../shared/breadcrumbs/breadcrumb.service';
import { NonComplianceService } from '../core/non-compliance.service';
import { resolveSectionStatus } from './section.status';

@Component({
  selector: 'app-daily-penalty-notice',
  templateUrl: './daily-penalty-notice.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DailyPenaltyNoticeComponent implements OnInit {
  sectionStatus$ = this.nonComplianceService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload)),
  );

  allowNotifyOperator$ = this.nonComplianceService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        resolveSectionStatus(data.requestTask.payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload) ===
          'complete' && data.allowedRequestTaskActions.includes('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_NOTIFY_OPERATOR'),
    ),
  );

  allowSendPeerReview$ = this.nonComplianceService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        resolveSectionStatus(data.requestTask.payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload) ===
          'complete' &&
        data.allowedRequestTaskActions.includes('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW'),
    ),
  );

  constructor(
    private readonly nonComplianceService: NonComplianceService,
    readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbService.cutLastBreadcrumbWithLinkandShow();
  }

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }
}
