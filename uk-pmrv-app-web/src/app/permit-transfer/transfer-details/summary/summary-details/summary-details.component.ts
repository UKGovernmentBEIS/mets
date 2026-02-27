import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PermitTransferDetailsConfirmation } from 'pmrv-api';

@Component({
  selector: 'app-transfer-details-summary-details-template',
  standalone: false,
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  @Input() hasBottomBorder = true;
  @Input() showChangeLink: boolean;
  @Input() transferDetailsConfirmation: PermitTransferDetailsConfirmation;
}
