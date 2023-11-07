import { Component, Input } from '@angular/core';

import { MeasurementOfCO2EmissionPointCategory } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-category-summary-overview',
  templateUrl: './category-summary-overview.component.html',
})
export class CategorySummaryOverviewComponent {
  @Input() emissionPointCategory: MeasurementOfCO2EmissionPointCategory;
  @Input() cssClass: string;
}
