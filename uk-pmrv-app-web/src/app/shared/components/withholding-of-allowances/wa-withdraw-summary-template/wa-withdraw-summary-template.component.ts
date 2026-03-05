import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-wa-withdraw-summary-template',
  templateUrl: './wa-withdraw-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WaWithdrawSummaryTemplateComponent {
  @Input() payload: WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload;

  @Input() isEditable: boolean;
}
