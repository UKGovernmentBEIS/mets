import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PermitNotificationService } from '../../../../core/permit-notification.service';

@Component({
  selector: 'app-decision-summary',
  templateUrl: './decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DecisionSummaryComponent {
  @Input() reviewDecision;

  constructor(public readonly permitNotificationService: PermitNotificationService) {}
}
