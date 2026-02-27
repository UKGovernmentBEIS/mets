import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaTimeAllocationScope } from 'pmrv-api';

@Component({
  selector: 'app-time-allocation-corsia-template',
  imports: [SharedModule, RouterLink],
  templateUrl: './time-allocation-corsia-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimeAllocationCorsiaTemplateComponent {
  @Input() data: AviationAerCorsiaTimeAllocationScope;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
