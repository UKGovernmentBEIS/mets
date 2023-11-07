import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { WithholdingAllowancesService } from '@tasks/withholding-allowances/core/withholding-allowances.service';

import { WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-withdraw-summary',
  templateUrl: './withdraw-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WithdrawSummaryComponent {
  isEditable$ = this.withholdingAllowancesService.isEditable$;
  payload$ = this.withholdingAllowancesService.payload$;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    private readonly withholdingAllowancesService: WithholdingAllowancesService,
  ) {}

  onConfirm() {
    this.withholdingAllowancesService.payload$
      .pipe(
        first(),
        switchMap((payload: WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload) => {
          return this.withholdingAllowancesService.postTaskSave(
            { withholdingWithdrawal: payload.withholdingWithdrawal },
            true,
            'WITHDRAWAL_REASON_CHANGE',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
