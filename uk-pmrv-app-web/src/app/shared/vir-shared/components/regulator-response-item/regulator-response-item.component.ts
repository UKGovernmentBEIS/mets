import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params } from '@angular/router';

import { RegulatorImprovementResponse } from 'pmrv-api';

@Component({
  selector: 'app-regulator-response-item',
  templateUrl: './regulator-response-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorResponseItemComponent {
  @Input() isEditable = false;
  @Input() reference: string;
  @Input() regulatorImprovementResponse: RegulatorImprovementResponse;
  @Input() isReview = false;
  @Input() isAviation = false;
  @Input() queryParams: Params = {};
}
