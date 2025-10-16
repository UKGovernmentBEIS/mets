import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { workflowLabelsMap } from './filters';

@Component({
  selector: 'app-dashboard-filters',
  templateUrl: './filters.component.html',
  styleUrl: './filters.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardFiltersComponent implements OnChanges, OnInit {
  @Input() order: 'NEWEST_FIRST' | 'NEAREST_DUE_DATE';
  @Input() filter: string;
  @Input() filterRequestTypes: string[];
  @Input() searchTerm: string = '';

  @Output() readonly orderByChange = new EventEmitter<'NEWEST_FIRST' | 'NEAREST_DUE_DATE'>();
  @Output() readonly filterByChange = new EventEmitter<string>();

  @Output() readonly searchByChange = new EventEmitter<string>();
  workflowDetailsTypesMap = workflowLabelsMap;
  sortedRequestTypes = [];

  form: UntypedFormGroup = this.fb.group(
    {
      search: [this.searchTerm ?? ''],
    },
    { updateOn: 'change' },
  );

  ngOnChanges() {
    this.sortedRequestTypes = (this.filterRequestTypes || []).slice().sort((a, b) => {
      const labelA = this.workflowDetailsTypesMap[a] ?? '';
      const labelB = this.workflowDetailsTypesMap[b] ?? '';

      return labelA.localeCompare(labelB, 'en-GB', { sensitivity: 'base' });
    });

    this.form.patchValue({ search: this.searchTerm });
  }

  ngOnInit(): void {
    this.form.patchValue({ search: this.searchTerm });
  }

  onOrderByChange(event: 'NEWEST_FIRST' | 'NEAREST_DUE_DATE') {
    this.orderByChange.emit(event);
  }

  onFilterByChange(event: string) {
    this.filterByChange.emit(event);
  }

  searchBy() {
    this.searchByChange.emit(this.form.value.search);
  }

  constructor(private readonly fb: UntypedFormBuilder) {}
}
