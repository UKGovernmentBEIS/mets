import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerCorsiaTotalEmissions } from 'pmrv-api';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-total-emissions-corsia-scheme-year-summary',
  standalone: true,
  imports: [CommonModule, GovukComponentsModule],
  templateUrl: './total-emissions-corsia-scheme-year-summary.component.html',
})
export class TotalEmissionsCorsiaSchemeYearSummaryComponent {
  @Input() totalEmissions: AviationAerCorsiaTotalEmissions;
}
