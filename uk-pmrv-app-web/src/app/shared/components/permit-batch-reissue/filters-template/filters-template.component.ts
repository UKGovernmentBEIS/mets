import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import {
  accountCategoryLabelMap,
  accountStatusLabelMap,
  accountTypeLabelMap,
  allocationStatusLabelMap,
} from '../../../../permit-batch-reissue/submit/filters-label.map';
import { FiltersModel } from '../filters.model';

@Component({
  selector: 'app-permit-batch-reissue-filters-template',
  standalone: false,
  templateUrl: './filters-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FiltersTemplateComponent implements OnInit {
  @Input() filters: FiltersModel;
  @Input() editable: boolean;

  readonly accountStatusLabelMap = accountStatusLabelMap;
  readonly accountTypeLabelMap = accountTypeLabelMap;
  readonly accountCategoryLabelMap = accountCategoryLabelMap;
  readonly allocationStatusLabelMap = allocationStatusLabelMap;

  allocationStatuses = [];

  ngOnInit(): void {
    if (this.filters.freeAllocation) {
      this.allocationStatuses.push('FREE_ALLOCATION');
    }
    if (this.filters.nonFreeAllocation) {
      this.allocationStatuses.push('NONFREE_ALLOCATION');
    }
  }
}
