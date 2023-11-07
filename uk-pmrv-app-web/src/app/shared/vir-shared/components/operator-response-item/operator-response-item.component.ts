import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params } from '@angular/router';

import { AttachedFile } from '@shared/types/attached-file.type';

import { OperatorImprovementResponse } from 'pmrv-api';

@Component({
  selector: 'app-operator-response-item',
  templateUrl: './operator-response-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorResponseItemComponent {
  @Input() isEditable = false;
  @Input() reference: string;
  @Input() operatorImprovementResponse: OperatorImprovementResponse;
  @Input() attachedFiles: AttachedFile[];
  @Input() isReview = false;
  @Input() isAviation = false;
  @Input() queryParams: Params = {};
}
