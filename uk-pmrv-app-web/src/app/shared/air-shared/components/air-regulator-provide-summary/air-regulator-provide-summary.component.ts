import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { RegulatorAirReviewResponse } from 'pmrv-api';

@Component({
  selector: 'app-air-regulator-provide-summary',
  templateUrl: './air-regulator-provide-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AirRegulatorProvideSummaryComponent {
  @Input() regulatorAirReviewResponse: RegulatorAirReviewResponse;
  @Input() isEditable = false;
  @Input() isReview = false;
}
