import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerDataGapsMethodologies } from 'pmrv-api';

@Component({
  selector: 'app-data-gaps-methodologies-group',
  templateUrl: './data-gaps-methodologies-group.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsMethodologiesGroupComponent {
  @Input() isEditable = false;
  @Input() dataGapsMethodologies: AviationAerDataGapsMethodologies;
  @Input() queryParams: Params = {};
}
