import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerDataGapsMethodologies } from 'pmrv-api';

@Component({
  selector: 'app-data-gaps-methodologies-group',
  imports: [SharedModule, RouterLinkWithHref],
  templateUrl: './data-gaps-methodologies-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsMethodologiesGroupComponent {
  @Input() isEditable = false;
  @Input() dataGapsMethodologies: AviationAerDataGapsMethodologies;
  @Input() queryParams: Params = {};
}
