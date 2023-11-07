import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { MethodologiesToCloseDataGaps } from 'pmrv-api';

@Component({
  selector: 'app-data-gaps-group',
  templateUrl: './data-gaps-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsGroupComponent {
  @Input() isEditable = false;
  @Input() dataGapsInfo: MethodologiesToCloseDataGaps;
}
