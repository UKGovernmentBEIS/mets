import { Component, Input } from '@angular/core';
import { Params, RouterLink, RouterLinkWithHref } from '@angular/router';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerTotalEmissionsConfidentiality } from 'pmrv-api';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-total-emissions-summary-template',
  templateUrl: './total-emissions-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, RouterLinkWithHref, RouterLink],
  viewProviders: [existingControlContainer],
})
export class TotalEmissionsSummaryTemplateComponent {
  @Input() data: AviationAerTotalEmissionsConfidentiality | null;
  @Input() isEditable = false;
  @Input() changeUrlQueryParams: Params = {};
}
