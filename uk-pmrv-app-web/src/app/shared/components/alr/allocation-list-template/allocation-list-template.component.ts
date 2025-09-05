import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { subInstallationNameLabelsMap } from '@shared/components/doal/activity-level-label.map';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import { ALRPreliminaryAllocation } from 'pmrv-api';

@Component({
  selector: 'app-alr-allocation-list-template',
  templateUrl: './allocation-list-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedModule, RouterLink],
})
export class AllocationListTemplateComponent implements OnInit {
  @Input() data: ALRPreliminaryAllocation[];
  @Input() editable: boolean;

  columns: GovukTableColumn[] = [];

  dataSorted: ALRPreliminaryAllocation[] = [];

  subInstallationNameLabelsMap = subInstallationNameLabelsMap;

  ngOnInit(): void {
    this.dataSorted = this.data?.sort((a, b) =>
      a.year - b.year === 0 ? a.subInstallationName.localeCompare(b.subInstallationName) : a.year - b.year,
    );

    this.columns = [
      { field: 'year', header: 'Year' },
      { field: 'subInstallationName', header: 'Sub-installation' },
      { field: 'allowances', header: 'Allocation', alignRight: true },
    ];

    if (this.editable) {
      this.columns = this.columns.concat({ field: 'change', header: '' }, { field: 'delete', header: '' });
    }
  }
}
