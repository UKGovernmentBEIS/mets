import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { VirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-verification-data-group-review',
  templateUrl: './verification-data-group-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationDataGroupReviewComponent {
  @Input() virPayload: VirApplicationReviewRequestTaskPayload;
}
