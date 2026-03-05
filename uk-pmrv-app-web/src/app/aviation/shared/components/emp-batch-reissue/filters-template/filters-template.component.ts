import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import {
  emissionTradingSchemeLabelMap,
  reportingStatusLabelMap,
} from '../../../../workflows/emp-batch-reissue/submit/filters-label.map';
import { FiltersModel } from '../filters.model';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-emp-batch-reissue-filters-template',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, RouterLink],
  templateUrl: './filters-template.component.html',
})
export class EmpBatchReissueFiltersTemplateComponent {
  @Input() filters: FiltersModel;
  @Input() editable: boolean;

  readonly reportingStatusLabelMap = reportingStatusLabelMap;
  readonly emissionTradingSchemeLabelMap = emissionTradingSchemeLabelMap;
}
