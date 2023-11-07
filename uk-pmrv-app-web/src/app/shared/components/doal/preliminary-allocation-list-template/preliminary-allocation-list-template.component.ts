import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { PreliminaryAllocation } from 'pmrv-api';

import { subInstallationNameLabelsMap } from '../activity-level-label.map';

@Component({
  selector: 'app-doal-preliminary-allocation-list-template',
  templateUrl: './preliminary-allocation-list-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreliminaryAllocationListTemplateComponent implements OnInit {
  @Input() data: PreliminaryAllocation[];
  @Input() editable: boolean;

  columns: GovukTableColumn[] = [];

  dataSorted: PreliminaryAllocation[];

  subInstallationNameLabelsMap = subInstallationNameLabelsMap;

  ngOnInit(): void {
    this.dataSorted = this.data.sort((a, b) =>
      a.year - b.year === 0 ? a.subInstallationName.localeCompare(b.subInstallationName) : a.year - b.year,
    );

    this.columns = [
      { field: 'year', header: 'Year' },
      { field: 'subInstallationName', header: 'Sub-installation' },
      { field: 'allowances', header: 'Allocation' },
    ];

    if (this.editable) {
      this.columns = this.columns.concat({ field: 'change', header: '' }, { field: 'delete', header: '' });
    }
  }
}
