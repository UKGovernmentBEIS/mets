import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-ra-summary-template',
  templateUrl: './ra-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RaSummaryTemplateComponent {
  @Input() payload: ReturnOfAllowancesApplicationSubmitRequestTaskPayload['returnOfAllowances'];
  @Input() isEditable: boolean;
}
