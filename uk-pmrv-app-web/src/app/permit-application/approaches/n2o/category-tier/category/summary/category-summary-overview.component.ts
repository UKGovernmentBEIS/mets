import { Component, Input } from '@angular/core';

import { MeasurementOfN2OEmissionPointCategory } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-category-summary-overview',
  templateUrl: './category-summary-overview.component.html',
})
export class CategorySummaryOverviewComponent {
  @Input() emissionPointCategory: MeasurementOfN2OEmissionPointCategory;
  @Input() cssClass: string;
}
