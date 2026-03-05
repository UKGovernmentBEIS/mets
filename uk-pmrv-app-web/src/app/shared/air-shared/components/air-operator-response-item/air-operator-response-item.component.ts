import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';
import { AttachedFile } from '@shared/types/attached-file.type';

@Component({
  selector: 'app-air-operator-response-item',
  templateUrl: './air-operator-response-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AirOperatorResponseItemComponent {
  @Input() isEditable = false;
  @Input() isReview = false;
  @Input() reference: string;
  @Input() operatorAirImprovementResponse: OperatorAirImprovementResponseAll;
  @Input() attachedFiles: AttachedFile[];
  @Input() resolvedChangeLink: string;
}
