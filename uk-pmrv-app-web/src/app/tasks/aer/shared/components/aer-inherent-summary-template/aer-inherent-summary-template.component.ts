import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ValidationErrors } from '@angular/forms';

import { InherentReceivingTransferringInstallation } from 'pmrv-api';

@Component({
  selector: 'app-aer-inherent-summary-template',
  standalone: false,
  templateUrl: './aer-inherent-summary-template.component.html',
  styleUrl: './aer-inherent-summary-template.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerInherentSummaryTemplateComponent {
  @Input() inherentInstallations: InherentReceivingTransferringInstallation[];
  @Input() isEditable: boolean;
  @Input() errors: ValidationErrors;
  @Input() isWizardComplete: (installation: InherentReceivingTransferringInstallation) => boolean;
}
