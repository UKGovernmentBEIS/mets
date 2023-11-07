import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DestroySubject } from '@core/services/destroy-subject.service';

@Component({
  selector: 'app-permit-notification-review-decision-summary-details[reviewDecision]',
  templateUrl: './review-decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ReviewDecisionSummaryComponent {
  @Input() reviewDecision;
  @Input() notesVisible = true;
}
