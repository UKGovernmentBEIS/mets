import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { InherentReceivingTransferringInstallation } from 'pmrv-api';

@Component({
  selector: 'app-inherent-co2-group',
  templateUrl: './inherent-co2-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentCo2GroupComponent {
  @Input() inherentInstallations: InherentReceivingTransferringInstallation[];
}
