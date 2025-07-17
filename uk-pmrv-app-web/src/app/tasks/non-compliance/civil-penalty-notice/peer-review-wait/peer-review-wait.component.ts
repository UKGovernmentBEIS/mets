import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { first, map } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { NonComplianceCivilPenaltyRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { NonComplianceService } from '../../core/non-compliance.service';
import { resolveSectionStatus } from '../section.status';

@Component({
  selector: 'app-peer-review-wait',
  templateUrl: './peer-review-wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewWaitComponent implements OnInit {
  sectionStatus$ = this.nonComplianceService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as NonComplianceCivilPenaltyRequestTaskPayload)),
  );

  constructor(
    readonly store: CommonTasksStore,
    private readonly nonComplianceService: NonComplianceService,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbService.cutLastBreadcrumbWithLinkandShow();
  }
}
