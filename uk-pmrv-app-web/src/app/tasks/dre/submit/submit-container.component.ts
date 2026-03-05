import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map } from 'rxjs';

import { DreApplicationSubmitRequestTaskPayload, DreRequestMetadata } from 'pmrv-api';

import { DreService } from '../core/dre.service';
import { resolveSectionStatus } from './section-status';

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  header$ = this.dreService.requestMetadata$.pipe(
    first(),
    map((metadata) => `Determine ${(metadata as DreRequestMetadata)?.year} reportable emissions`),
  );

  sectionStatus$ = this.dreService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as DreApplicationSubmitRequestTaskPayload)),
  );

  allowNotifyOperator$ = this.dreService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        resolveSectionStatus(data.requestTask.payload as DreApplicationSubmitRequestTaskPayload) === 'complete' &&
        data.allowedRequestTaskActions.includes('DRE_SUBMIT_NOTIFY_OPERATOR'),
    ),
  );

  allowSendPeerReview$ = this.dreService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        resolveSectionStatus(data.requestTask.payload as DreApplicationSubmitRequestTaskPayload) === 'complete' &&
        data.allowedRequestTaskActions.includes('DRE_REQUEST_PEER_REVIEW'),
    ),
  );

  canViewSectionDetails$ = combineLatest([this.dreService.isEditable$, this.sectionStatus$]).pipe(
    map(([isEditable, sectionStatus]) => isEditable || sectionStatus !== 'not started'),
  );

  constructor(
    private readonly dreService: DreService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
  ) {}

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }
}
