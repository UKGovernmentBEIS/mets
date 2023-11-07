import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SummaryOfConditions } from 'pmrv-api';

@Component({
  selector: 'app-summary-of-conditions-group',
  templateUrl: './summary-of-conditions-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryOfConditionsGroupComponent {
  @Input() isEditable = false;
  @Input() summaryOfConditionsInfo: SummaryOfConditions;
}
