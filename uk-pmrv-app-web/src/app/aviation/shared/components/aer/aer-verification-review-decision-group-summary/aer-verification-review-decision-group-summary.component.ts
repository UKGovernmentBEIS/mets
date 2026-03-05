import { Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { AerVerificationReportDataReviewDecision } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-aer-verification-review-decision-group-summary',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './aer-verification-review-decision-group-summary.component.html',
})
export class AerVerificationReviewDecisionGroupSummaryComponent {
  @Input() data: AerVerificationReportDataReviewDecision;

  readonly aerReviewGroupDecision: Record<AerVerificationReportDataReviewDecision['type'], string> = {
    ACCEPTED: 'Accepted',
  };
}
