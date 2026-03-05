import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map } from 'rxjs';

import { WithholdingOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { WithholdingAllowancesService } from '../../../core/withholding-allowances.service';

@Component({
  selector: 'app-summary-details',
  template: `
    <app-wa-summary-template [payload]="payload$ | async" [isEditable]="isEditable$ | async"></app-wa-summary-template>
    <a govukLink routerLink="..">Return to: Peer review withholding of allowances</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  isEditable$ = this.withholdingAllowancesService.isEditable$;

  payload$ = this.withholdingAllowancesService.payload$.pipe(
    first(),
    map((payload) => payload as WithholdingOfAllowancesApplicationSubmitRequestTaskPayload),
  );

  constructor(readonly withholdingAllowancesService: WithholdingAllowancesService) {}
}
