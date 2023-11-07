import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import {
  accountCategoryLabelMap,
  accountStatusLabelMap,
  accountTypeLabelMap,
} from '../../../../permit-batch-reissue/submit/filters-label.map';
import { FiltersModel } from '../filters.model';

@Component({
  selector: 'app-permit-batch-reissue-filters-template',
  templateUrl: './filters-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FiltersTemplateComponent {
  @Input() filters: FiltersModel;
  @Input() editable: boolean;

  readonly accountStatusLabelMap = accountStatusLabelMap;
  readonly accountTypeLabelMap = accountTypeLabelMap;
  readonly accountCategoryLabelMap = accountCategoryLabelMap;
}
