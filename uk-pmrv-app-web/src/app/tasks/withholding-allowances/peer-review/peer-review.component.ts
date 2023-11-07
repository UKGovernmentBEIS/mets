import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { WithholdingAllowancesService } from '@tasks/withholding-allowances/core/withholding-allowances.service';
import { getSectionStatus } from '@tasks/withholding-allowances/submit/submit';

import { WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';

@Component({
  selector: 'app-peer-review',
  templateUrl: './peer-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewComponent {
  sectionStatus$ = this.withholdingAllowancesService.payload$.pipe(
    first(),
    map((payload) => getSectionStatus(payload as WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload)),
  );

  readonly allowPeerReviewDecision$: Observable<boolean> = this.store.pipe(
    first(),
    map((state) => {
      return state.requestTaskItem.allowedRequestTaskActions?.includes(
        'WITHHOLDING_OF_ALLOWANCES_SUBMIT_PEER_REVIEW_DECISION',
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
    private readonly withholdingAllowancesService: WithholdingAllowancesService,
  ) {}
}
