import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-aer-emission-details-group',
  templateUrl: './aer-emission-details-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerEmissionDetailsGroupComponent {
  @Input() monitoringApproachDescription?: OpinionStatement['monitoringApproachDescription'];
  @Input() emissionFactorsDescription?: OpinionStatement['emissionFactorsDescription'];
  @Input() isEditable: boolean;
  @Input() monitoringChangeLink = 'change';
  @Input() emissionFactorsChangeLink = 'change';
}
