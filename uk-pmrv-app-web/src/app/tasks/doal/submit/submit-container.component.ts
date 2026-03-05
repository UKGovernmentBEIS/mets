import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { DoalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { DoalService } from '../core/doal.service';
import { canComplete, canNotifyOperator, canSendPeerReview } from './submit-actions';

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  header$ = this.doalService.requestMetadata$.pipe(
    first(),
    map((metadata) => `${metadata?.year} Determination of activity level change`),
  );

  allowNotifyOperator$ = this.doalService.requestTaskItem$.pipe(
    map((requestTaskItem) =>
      canNotifyOperator(
        requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload,
        requestTaskItem.allowedRequestTaskActions,
      ),
    ),
  );

  allowComplete$ = this.doalService.requestTaskItem$.pipe(
    map((requestTaskItem) =>
      canComplete(
        requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload,
        requestTaskItem.allowedRequestTaskActions,
      ),
    ),
  );

  allowSendPeerReview$ = this.doalService.requestTaskItem$.pipe(
    map((requestTaskItem) =>
      canSendPeerReview(
        requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload,
        requestTaskItem.allowedRequestTaskActions,
      ),
    ),
  );

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  complete(): void {
    this.router.navigate(['complete'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }

  constructor(
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
