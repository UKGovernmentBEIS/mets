import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import {
  WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload,
  WithholdingOfAllowancesApplicationSubmitRequestTaskPayload,
  WithholdingOfAllowancesApplicationSubmittedRequestActionPayload,
} from 'pmrv-api';

@Component({
  selector: 'app-wa-summary-template',
  templateUrl: './wa-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WaSummaryTemplateComponent {
  @Input() payload:
    | WithholdingOfAllowancesApplicationSubmitRequestTaskPayload
    | WithholdingOfAllowancesApplicationSubmittedRequestActionPayload
    | WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload;
  @Input() isEditable: boolean;
}
