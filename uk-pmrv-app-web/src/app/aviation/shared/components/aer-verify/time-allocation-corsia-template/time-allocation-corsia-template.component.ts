import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaTimeAllocationScope } from 'pmrv-api';

@Component({
  selector: 'app-time-allocation-corsia-template',
  templateUrl: './time-allocation-corsia-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimeAllocationCorsiaTemplateComponent {
  @Input() data: AviationAerCorsiaTimeAllocationScope;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
