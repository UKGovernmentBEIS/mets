import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AttachedFile } from '@shared/types/attached-file.type';

import { OperatorAirImprovementFollowUpResponse } from 'pmrv-api';

@Component({
  selector: 'app-air-operator-followup-item',
  templateUrl: './air-operator-followup-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AirOperatorFollowupItemComponent {
  @Input() isEditable = false;
  @Input() reference: string;
  @Input() operatorAirImprovementFollowUpResponse: OperatorAirImprovementFollowUpResponse;
  @Input() attachedFiles: AttachedFile[];
  @Input() isReview = false;
}
