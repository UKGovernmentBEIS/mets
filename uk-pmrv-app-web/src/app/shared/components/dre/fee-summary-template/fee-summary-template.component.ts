import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DreFee } from 'pmrv-api';

import { calculateTotalFee } from '../../../../tasks/dre/submit/fee/fee';

@Component({
  selector: 'app-fee-summary-template',
  templateUrl: './fee-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeeSummaryTemplateComponent {
  @Input() fee: DreFee;
  @Input() editable: boolean;

  get totalFee(): string {
    return calculateTotalFee(this.fee.feeDetails);
  }
}
