import { Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { EmpAcceptedVariationDecisionDetails } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-emp-variation-regulator-led-decision-group-summary',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './emp-variation-regulator-led-decision-group-summary.component.html',
})
export class EmpVariationRegulatorLedDecisionGroupSummaryComponent {
  @Input() data: EmpAcceptedVariationDecisionDetails;
}
