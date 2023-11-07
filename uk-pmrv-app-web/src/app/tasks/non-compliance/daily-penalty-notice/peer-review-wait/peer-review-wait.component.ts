import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map } from 'rxjs';

import { NonComplianceDailyPenaltyNoticeRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { NonComplianceService } from '../../core/non-compliance.service';
import { resolveSectionStatus } from '../section.status';

@Component({
  selector: 'app-peer-review-wait',
  templateUrl: './peer-review-wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewWaitComponent {
  sectionStatus$ = this.nonComplianceService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload)),
  );

  constructor(readonly store: CommonTasksStore, private readonly nonComplianceService: NonComplianceService) {}
}
