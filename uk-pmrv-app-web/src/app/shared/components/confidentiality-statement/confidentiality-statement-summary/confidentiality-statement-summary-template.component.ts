import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { ConfidentialityStatement } from 'pmrv-api';

@Component({
  selector: 'app-confidentiality-statement-summary-template',
  templateUrl: './confidentiality-statement-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfidentialityStatementSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() cssClass: string;
  @Input() hasBottomBorder = true;
  @Input() data: ConfidentialityStatement;
}
