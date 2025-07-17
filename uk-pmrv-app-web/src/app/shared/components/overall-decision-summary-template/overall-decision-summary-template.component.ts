import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';

@Component({
  selector: 'app-shared-overall-decision-summary-template',
  templateUrl: './overall-decision-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() overallDecision: OverallVerificationAssessment;
  @Input() notes: string;
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;
  constructor() {}
}
