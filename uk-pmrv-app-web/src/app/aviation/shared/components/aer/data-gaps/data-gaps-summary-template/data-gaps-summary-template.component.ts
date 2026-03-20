import { Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerDataGaps } from 'pmrv-api';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-data-gaps-summary-template',
  imports: [SharedModule, GovukComponentsModule, RouterLinkWithHref],
  templateUrl: './data-gaps-summary-template.component.html',
})
export class DataGapsSummaryTemplateComponent {
  @Input() data: AviationAerDataGaps;
  @Input() isEditable = false;
  @Input() changeUrlQueryParams: Params = {};
}
