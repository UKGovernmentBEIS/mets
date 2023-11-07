import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { ActivityLevelChangeInformation } from 'pmrv-api';

@Component({
  selector: 'app-doal-alc-information-template',
  templateUrl: './alc-information-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlcInformationTemplateComponent {
  @Input() data: ActivityLevelChangeInformation;
  @Input() editable: boolean;
}
