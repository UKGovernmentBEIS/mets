import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { NonComplianceNoticeOfIntentRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { NonComplianceService } from '../../core/non-compliance.service';
import { resolveSectionStatus } from '../section.status';

@Component({
  selector: 'app-peer-review',
  templateUrl: './peer-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewComponent implements OnInit {
  readonly allowPeerReviewDecision$: Observable<boolean> = this.store.pipe(
    first(),
    map((state) => {
      return state.requestTaskItem.allowedRequestTaskActions?.includes(
        'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION',
      );
    }),
  );

  sectionStatus$ = this.nonComplianceService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as NonComplianceNoticeOfIntentRequestTaskPayload)),
  );

  constructor(
    readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly nonComplianceService: NonComplianceService,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  peerReviewDecision(): void {
    this.router.navigate(['./decision'], { relativeTo: this.route });
  }

  ngOnInit(): void {
    this.breadcrumbService.cutLastBreadcrumbWithLinkandShow();
  }
}
