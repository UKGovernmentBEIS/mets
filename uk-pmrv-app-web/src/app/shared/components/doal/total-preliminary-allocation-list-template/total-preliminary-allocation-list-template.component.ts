import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { getTotalAllocationsPerYear } from '@shared/components/doal/total-preliminary-allocation-list-template/total-preliminary-allocation-list.util';

import { GovukTableColumn } from 'govuk-components';

import { PreliminaryAllocation } from 'pmrv-api';

@Component({
  selector: 'app-doal-total-preliminary-allocation-list-template',
  template: `
    <ng-container *ngIf="totalAllocations.length; else noResults">
      <govuk-table [columns]="columns" [data]="totalAllocations">
        <ng-template let-column="column" let-row="row" let-index="index">
          <ng-container [ngSwitch]="column.field">
            <ng-container *ngSwitchCase="'year'">
              {{ row.year }}
            </ng-container>
            <ng-container *ngSwitchCase="'allowances'">
              {{ row.allowances }}
            </ng-container>
          </ng-container>
        </ng-template>
      </govuk-table>
    </ng-container>
    <ng-template #noResults>
      <div class="govuk-body"><h2 class="govuk-heading-s">No results</h2></div>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalPreliminaryAllocationListTemplateComponent implements OnInit {
  @Input() data: PreliminaryAllocation[];

  columns: GovukTableColumn[] = [
    { field: 'year', header: 'Year' },
    { field: 'allowances', header: 'Approved allocation' },
  ];
  totalAllocations: { year: number; allowances: number }[] = [];

  ngOnInit(): void {
    this.totalAllocations = getTotalAllocationsPerYear(this.data);
  }
}
