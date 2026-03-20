import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-permit-transfer-details-summary-template',
  standalone: false,
  templateUrl: './transfer-details-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitTransferDetailsSummaryTemplateComponent {
  @Input() allowChange: boolean;
  @Input() payload: PermitTransferAApplicationRequestTaskPayload;
  @Input() files: { downloadUrl: string; fileName: string }[] = [];
}
