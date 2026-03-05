import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { WithholdingOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { WithholdingAllowancesService } from '../../core/withholding-allowances.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
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
        switchMap((payload: WithholdingOfAllowancesApplicationSubmitRequestTaskPayload) => {
          return this.withholdingAllowancesService.postTaskSave(
            { withholdingOfAllowances: payload.withholdingOfAllowances },
            true,
            'DETAILS_CHANGE',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
