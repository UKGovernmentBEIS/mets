import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params } from '@angular/router';

import { OperatorImprovementFollowUpResponse } from 'pmrv-api';

@Component({
  selector: 'app-operator-followup-item',
  templateUrl: './operator-followup-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorFollowupItemComponent {
  @Input() isEditable = false;
  @Input() reference: string;
  @Input() operatorImprovementFollowUpResponse: OperatorImprovementFollowUpResponse;
  @Input() isReview = false;
  @Input() isAviation = false;
  @Input() queryParams: Params = {};
}
