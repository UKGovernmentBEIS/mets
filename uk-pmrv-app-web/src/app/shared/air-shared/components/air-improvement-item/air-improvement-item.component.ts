import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';

@Component({
  selector: 'app-air-improvement-item',
  templateUrl: './air-improvement-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AirImprovementItemComponent {
  @Input() reference: string;
  @Input() airImprovement: AirImprovementAll;
  @Input() isSummary = false;
}
