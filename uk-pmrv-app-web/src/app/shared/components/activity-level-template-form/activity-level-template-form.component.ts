import { ChangeDetectionStrategy, Component } from '@angular/core';

import { changeTypeLabelsMap, subInstallationNameLabelsMap } from '@shared/components/doal/activity-level-label.map';
import { existingControlContainer } from '@shared/providers/control-container.factory';

@Component({
  selector: 'app-activity-level-template-form',
  standalone: false,
  templateUrl: './activity-level-template-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  viewProviders: [existingControlContainer],
})
export class ActivityLevelTemplateFormComponent {
  readonly years = Array.from({ length: 2030 - 2021 + 1 }, (_, i) => 2021 + i).map((year) => ({
    text: `${year}`,
    value: `${year}`,
  }));

  subInstallationNameLabelsMap = subInstallationNameLabelsMap;
  changeTypeLabelsMap = changeTypeLabelsMap;
}
