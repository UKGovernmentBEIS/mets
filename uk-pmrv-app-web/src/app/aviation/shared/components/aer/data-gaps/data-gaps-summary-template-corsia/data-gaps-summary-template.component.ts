import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerCorsiaDataGaps } from 'pmrv-api';

@Component({
  selector: 'app-data-gaps-summary-template',
  templateUrl: './data-gaps-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, RouterLinkWithHref],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsSummaryTemplateComponent {
  @Input() data: AviationAerCorsiaDataGaps;
  @Input() affectedFlightsDataGaps: number;
  @Input() isEditable = false;
  @Input() changeUrlQueryParams: Params = {};
}
