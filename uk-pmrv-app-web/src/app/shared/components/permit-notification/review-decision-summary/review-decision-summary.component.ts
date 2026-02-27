import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DestroySubject } from '@core/services/destroy-subject.service';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'app-permit-notification-review-decision-summary-details[reviewDecision]',
  standalone: false,
  templateUrl: './review-decision-summary.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewDecisionSummaryComponent {
  @Input() reviewDecision;
  @Input() notesVisible = true;
}
