import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { WithholdingOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { WithholdingAllowancesService } from '../core/withholding-allowances.service';
import { getSectionStatus } from './submit';

@Component({
  selector: 'app-submit',
  templateUrl: './submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitComponent {
  sectionStatus$ = this.withholdingAllowancesService.payload$.pipe(
    first(),
    map((payload) => getSectionStatus(payload as WithholdingOfAllowancesApplicationSubmitRequestTaskPayload)),
  );

  canViewSectionDetails$: Observable<boolean> = combineLatest([
    this.withholdingAllowancesService.isEditable$,
    this.sectionStatus$,
  ]).pipe(map(([isEditable, sectionStatus]) => isEditable || sectionStatus === 'complete'));

  allowNotifyOperator$ = this.withholdingAllowancesService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        getSectionStatus(data.requestTask.payload as WithholdingOfAllowancesApplicationSubmitRequestTaskPayload) ===
          'complete' && data.allowedRequestTaskActions.includes('WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION'),
    ),
  );

  allowSendPeerReview$ = this.withholdingAllowancesService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        getSectionStatus(data.requestTask.payload as WithholdingOfAllowancesApplicationSubmitRequestTaskPayload) ===
          'complete' && data.allowedRequestTaskActions.includes('WITHHOLDING_OF_ALLOWANCES_REQUEST_PEER_REVIEW'),
    ),
  );

  constructor(
    private readonly withholdingAllowancesService: WithholdingAllowancesService,
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
