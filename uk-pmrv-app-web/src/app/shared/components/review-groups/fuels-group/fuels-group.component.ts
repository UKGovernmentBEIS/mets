import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { Aer, EmissionPoint, EmissionSource } from 'pmrv-api';

@Component({
  selector: 'app-fuels-group',
  templateUrl: './fuels-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelsGroupComponent {
  @Input() aerData: Aer;

  pointsAndSourcesColumns: GovukTableColumn<EmissionPoint | EmissionSource>[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-half' },
    { field: 'description', header: 'Description', widthClass: 'govuk-!-width-one-half' },
  ];
}
