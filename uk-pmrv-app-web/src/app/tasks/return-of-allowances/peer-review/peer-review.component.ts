import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';
import { ReturnOfAllowancesService } from '../core/return-of-allowances.service';
import { resolveSectionStatus } from '../core/section-status';

@Component({
  selector: 'app-peer-review',
  templateUrl: './peer-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewComponent {
  readonly allowPeerReviewDecision$: Observable<boolean> = this.store.pipe(
    first(),
    map((state) => {
      return state.requestTaskItem.allowedRequestTaskActions?.includes(
        'RETURN_OF_ALLOWANCES_SUBMIT_PEER_REVIEW_DECISION',
      );
    }),
  );

  sectionStatus$ = this.returnOfAllowancesService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload)),
  );

  constructor(
    readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly returnOfAllowancesService: ReturnOfAllowancesService,
  ) {}

  peerReviewDecision(): void {
    this.router.navigate(['./decision'], { relativeTo: this.route });
  }
}
