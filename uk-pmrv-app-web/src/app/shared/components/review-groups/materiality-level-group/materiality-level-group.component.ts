import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { MaterialityLevel } from 'pmrv-api';

@Component({
  selector: 'app-materiality-level-group',
  standalone: false,
  templateUrl: './materiality-level-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MaterialityLevelGroupComponent {
  @Input() isEditable = false;
  @Input() materialityLevelInfo: MaterialityLevel;
  @Input() isMaterialityUpdated = false;
}
