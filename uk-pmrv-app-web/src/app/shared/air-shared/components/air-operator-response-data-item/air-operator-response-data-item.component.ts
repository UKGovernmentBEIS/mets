import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';

import { OperatorAirImprovementResponse } from 'pmrv-api';

@Component({
  selector: 'app-air-operator-response-data-item',
  templateUrl: './air-operator-response-data-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AirOperatorResponseDataItemComponent {
  improvementResponse: OperatorAirImprovementResponseAll;

  @Input() set operatorImprovementResponse(value: OperatorAirImprovementResponse) {
    this.improvementResponse = value as OperatorAirImprovementResponseAll;
  }
}
