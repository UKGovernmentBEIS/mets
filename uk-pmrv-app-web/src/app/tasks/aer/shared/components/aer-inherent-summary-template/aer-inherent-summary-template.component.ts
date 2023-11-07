import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ValidationErrors } from '@angular/forms';

import { InherentReceivingTransferringInstallation } from 'pmrv-api';

@Component({
  selector: 'app-aer-inherent-summary-template',
  templateUrl: './aer-inherent-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./aer-inherent-summary-template.component.scss'],
})
export class AerInherentSummaryTemplateComponent {
  @Input() inherentInstallations: InherentReceivingTransferringInstallation[];
  @Input() isEditable: boolean;
  @Input() errors: ValidationErrors;
  @Input() isWizardComplete: (installation: InherentReceivingTransferringInstallation) => boolean;
}
