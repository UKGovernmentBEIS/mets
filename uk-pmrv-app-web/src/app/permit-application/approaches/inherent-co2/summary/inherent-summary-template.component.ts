import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ValidationErrors } from '@angular/forms';

import { InherentReceivingTransferringInstallation } from 'pmrv-api';

import { isWizardComplete } from '../inherent-co2-wizard';

@Component({
  selector: 'app-inherent-summary-template',
  standalone: false,
  templateUrl: './inherent-summary-template.component.html',
  styleUrl: './inherent-summary-template.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentSummaryTemplateComponent {
  @Input() data: Array<InherentReceivingTransferringInstallation>;
  @Input() isEditable: boolean;
  @Input() errors: ValidationErrors;

  isWizardComplete = isWizardComplete;
}
