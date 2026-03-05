import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

import { Observable } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { AviationAerTotalEmissions } from 'pmrv-api';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-total-emissions-scheme-year-summary',
  standalone: true,
  imports: [CommonModule, SharedModule],
  templateUrl: './total-emissions-scheme-year-summary.component.html',
})
export class TotalEmissionsSchemeYearSummaryComponent {
  @Input() totalEmissions$: Observable<AviationAerTotalEmissions>;
}
