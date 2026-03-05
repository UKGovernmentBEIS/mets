import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PeerReviewDecision, RequestActionDTO } from 'pmrv-api';

@Component({
  selector: 'app-peer-review-decision-template',
  templateUrl: './peer-review-decision-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewDecisionTemplateComponent {
  @Input() requestActionType: RequestActionDTO['type'];
  @Input() decision: PeerReviewDecision;
  @Input() submitter: string;
}
