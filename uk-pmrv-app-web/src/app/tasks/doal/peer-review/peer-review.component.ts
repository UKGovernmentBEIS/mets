import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { DoalService } from '@tasks/doal/core/doal.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

@Component({
  selector: 'app-peer-review',
  templateUrl: './peer-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewComponent {
  title = this.itemNamePipe.transform('DOAL_APPLICATION_PEER_REVIEW');
  allowPeerReviewDecision$: Observable<boolean> = this.store.pipe(
    first(),
    map((state) => {
      return state.requestTaskItem.allowedRequestTaskActions?.includes('DOAL_SUBMIT_PEER_REVIEW_DECISION');
    }),
  );

  constructor(
    readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly doalService: DoalService,
    private readonly itemNamePipe: ItemNamePipe,
  ) {}

  peerReviewDecision(): void {
    this.router.navigate(['./decision'], { relativeTo: this.route });
  }
}
