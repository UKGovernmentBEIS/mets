import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { NonComplianceCivilPenaltyRequestTaskPayload } from 'pmrv-api';

import { BreadcrumbService } from '../../../shared/breadcrumbs/breadcrumb.service';
import { NonComplianceService } from '../core/non-compliance.service';
import { resolveSectionStatus } from './section.status';

@Component({
  selector: 'app-civil-penalty-notice',
  templateUrl: './civil-penalty-notice.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CivilPenaltyNoticeComponent implements OnInit {
  sectionStatus$ = this.nonComplianceService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as NonComplianceCivilPenaltyRequestTaskPayload)),
  );

  allowNotifyOperator$ = this.nonComplianceService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        resolveSectionStatus(data.requestTask.payload as NonComplianceCivilPenaltyRequestTaskPayload) === 'complete' &&
        data.allowedRequestTaskActions.includes('NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR'),
    ),
  );

  allowSendPeerReview$ = this.nonComplianceService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        resolveSectionStatus(data.requestTask.payload as NonComplianceCivilPenaltyRequestTaskPayload) === 'complete' &&
        data.allowedRequestTaskActions.includes('NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW'),
    ),
  );

  constructor(
    private readonly nonComplianceService: NonComplianceService,
    readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbService.clear();
  }

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }
}
