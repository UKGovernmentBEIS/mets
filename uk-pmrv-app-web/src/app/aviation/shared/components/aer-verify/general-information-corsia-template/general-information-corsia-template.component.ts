import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaGeneralInformation } from 'pmrv-api';

@Component({
  selector: 'app-general-information-corsia-template',
  templateUrl: './general-information-corsia-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GeneralInformationCorsiaTemplateComponent {
  @Input() data: AviationAerCorsiaGeneralInformation;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
