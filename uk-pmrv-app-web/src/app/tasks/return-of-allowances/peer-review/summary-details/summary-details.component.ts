import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map } from 'rxjs';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { ReturnOfAllowancesService } from '../../core/return-of-allowances.service';

@Component({
  selector: 'app-summary-details',
  template: `
    <h2 class="govuk-heading-l">Details of return of allowances</h2>
    <app-ra-summary-template [payload]="payload$ | async" [isEditable]="isEditable$ | async"></app-ra-summary-template>
    <a govukLink routerLink="..">Return to: Peer review return of allowances</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  isEditable$ = this.returnOfAllowancesService.isEditable$;

  payload$ = this.returnOfAllowancesService.getPayload().pipe(
    first(),
    map((payload) => (payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload).returnOfAllowances),
  );

  constructor(readonly returnOfAllowancesService: ReturnOfAllowancesService) {}
}
