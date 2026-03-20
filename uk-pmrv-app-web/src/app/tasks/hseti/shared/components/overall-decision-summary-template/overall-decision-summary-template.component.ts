import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { HSETIRegulatorReviewOverallDecision } from 'pmrv-api';

@Component({
  selector: 'app-overall-decision-summary-template',
  imports: [SharedModule, TaskSharedModule, RouterLink],
  templateUrl: './overall-decision-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() overallDecision: HSETIRegulatorReviewOverallDecision;
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;
  constructor() {}
}
