import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { TransferCO2, TransferN2O } from 'pmrv-api';

@Component({
  selector: 'app-transfer-installation-summary-template',
  templateUrl: './transfer-installation-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferInstallationSummaryTemplateComponent {
  @Input() transfer: TransferN2O | TransferCO2;
  @Input() N2Otype: boolean;
  @Input() cssClass: string;
  @Input() hasBorders = false;
  @Input() isEditable = false;
}
