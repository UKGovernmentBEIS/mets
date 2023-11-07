import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { MaterialityLevel } from 'pmrv-api';

@Component({
  selector: 'app-materiality-level-group',
  templateUrl: './materiality-level-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MaterialityLevelGroupComponent {
  @Input() isEditable = false;
  @Input() materialityLevelInfo: MaterialityLevel;
}
