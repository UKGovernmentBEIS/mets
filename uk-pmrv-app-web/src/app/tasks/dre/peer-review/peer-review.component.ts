import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { DreApplicationSubmitRequestTaskPayload, DreRequestMetadata } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';
import { DreService } from '../core/dre.service';
import { getPreviewDocumentsInfo } from '../shared/previewDocumentsDre.util';
import { resolveSectionStatus } from '../submit/section-status';

@Component({
  selector: 'app-peer-review',
  templateUrl: './peer-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewComponent {
  previewDocuments = getPreviewDocumentsInfo('DRE_SUBMIT_PEER_REVIEW_DECISION');

  header$ = this.dreService.requestMetadata$.pipe(
    map((metadata) => `Peer review ${(metadata as DreRequestMetadata)?.year} reportable emissions`),
  );

  readonly allowPeerReviewDecision$: Observable<boolean> = this.store.pipe(
    first(),
    map((state) => {
      return state.requestTaskItem.allowedRequestTaskActions?.includes('DRE_SUBMIT_PEER_REVIEW_DECISION');
    }),
  );

  sectionStatus$ = this.dreService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as DreApplicationSubmitRequestTaskPayload)),
  );

  constructor(
    readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly dreService: DreService,
  ) {}

  peerReviewDecision(): void {
    this.router.navigate(['./decision'], { relativeTo: this.route });
  }
}
