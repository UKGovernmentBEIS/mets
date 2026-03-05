import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params } from '@angular/router';

import { RegulatorReviewResponse } from 'pmrv-api';

@Component({
  selector: 'app-regulator-create-summary',
  templateUrl: './regulator-create-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorCreateSummaryComponent {
  @Input() regulatorReviewResponse: RegulatorReviewResponse;
  @Input() isEditable = false;
  @Input() isReview = false;
  @Input() queryParams: Params = {};
}
