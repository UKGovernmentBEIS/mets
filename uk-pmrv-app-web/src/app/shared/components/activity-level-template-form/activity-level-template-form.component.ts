import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import {
  alrChangeTypeLabelsMap,
  alrChangeTypeLabelsMap2027,
  doalChangeTypeLabelsMap,
  newSubInstallationNameLabelsMap,
  subInstallationNameLabelsMap,
  subInstallationNameLabelsMap2027,
} from '@shared/components/doal/activity-level-label.map';
import { existingControlContainer } from '@shared/providers/control-container.factory';

import { ActivityLevel } from 'pmrv-api';

@Component({
  selector: 'app-activity-level-template-form',
  standalone: false,
  templateUrl: './activity-level-template-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  viewProviders: [existingControlContainer],
})
export class ActivityLevelTemplateFormComponent implements OnInit {
  @Input() isDoal: boolean;
  @Input() year: number;
  changeTypeLabelsMap: Omit<Partial<Record<ActivityLevel['changeType'], string>>, 'OTHER'>;
  hint: string;

  readonly years = Array.from({ length: 2030 - 2021 + 1 }, (_, i) => 2021 + i).map((year) => ({
    text: `${year}`,
    value: `${year}`,
  }));

  subInstallationNameLabelsMap;

  ngOnInit(): void {
    this.changeTypeLabelsMap = this.isDoal
      ? doalChangeTypeLabelsMap
      : this.year >= 2027
        ? alrChangeTypeLabelsMap2027
        : alrChangeTypeLabelsMap;
    this.hint = this.isDoal
      ? `Provide detailed information about this change.
    If Article 6a or Article 3z reasons apply, include specific details of temporary or permanent cessations.
    For new entrant reserve, provide information about the activity in the respective years, particularly where the HAL is being set.
    <br/><br/>This will be entered into the official notice.`
      : `Provide detailed information about this change. If Article 6a or Article 3za reasons apply, include specific details of temporary or permanent cessations.<br/><br/>
      This will be entered into the official notice`;

    this.subInstallationNameLabelsMap = this.isDoal
      ? newSubInstallationNameLabelsMap
      : this.year >= 2027
        ? subInstallationNameLabelsMap2027
        : subInstallationNameLabelsMap;
  }
}
