import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map } from 'rxjs';

import { DreApplicationSubmitRequestTaskPayload, DreRequestMetadata } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';
import { DreService } from '../core/dre.service';
import { resolveSectionStatus } from '../submit/section-status';

@Component({
  selector: 'app-peer-review-wait',
  templateUrl: './peer-review-wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewWaitComponent {
  header$ = this.dreService.requestMetadata$.pipe(
    map((metadata) => `${(metadata as DreRequestMetadata)?.year} reportable emissions sent to peer reviewer`),
  );

  sectionStatus$ = this.dreService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as DreApplicationSubmitRequestTaskPayload)),
  );

  constructor(
    readonly store: CommonTasksStore,
    private readonly dreService: DreService,
  ) {}
}
