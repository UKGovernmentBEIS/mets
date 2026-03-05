import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map } from 'rxjs';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { ReturnOfAllowancesService } from '../core/return-of-allowances.service';
import { resolveSectionStatus } from '../core/section-status';

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  sectionStatus$ = this.returnOfAllowancesService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload)),
  );

  allowNotifyOperator$ = this.returnOfAllowancesService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        resolveSectionStatus(data.requestTask.payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload) ===
          'complete' && data.allowedRequestTaskActions.includes('RETURN_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION'),
    ),
  );

  allowSendPeerReview$ = this.returnOfAllowancesService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        resolveSectionStatus(data.requestTask.payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload) ===
          'complete' && data.allowedRequestTaskActions.includes('RETURN_OF_ALLOWANCES_REQUEST_PEER_REVIEW'),
    ),
  );

  canViewSectionDetails$ = combineLatest([this.returnOfAllowancesService.isEditable$, this.sectionStatus$]).pipe(
    map(([isEditable, sectionStatus]) => !isEditable && sectionStatus === 'not started'),
  );

  constructor(
    private readonly returnOfAllowancesService: ReturnOfAllowancesService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
  ) {}

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.router.navigate(['../', 'peer-review'], { relativeTo: this.route });
  }
}
