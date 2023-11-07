import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { SourceStream } from 'pmrv-api';

@Component({
  selector: 'app-source-streams-summary-table',
  template: `
    <govuk-table [columns]="columns" [data]="data" [class.no-bottom-border]="!bottomBorder">
      <ng-template let-column="column" let-row="row">
        <ng-container [ngSwitch]="column.field">
          <ng-container *ngSwitchCase="'reference'">{{ row.reference }}</ng-container>
          <ng-container *ngSwitchCase="'description'">
            {{ row | sourceStreamDescription }}
          </ng-container>
          <ng-container *ngSwitchCase="'type'">{{ row.type | sourceStreamType }}</ng-container>
        </ng-container>
      </ng-template>
    </govuk-table>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SourceStreamsSummaryTableComponent {
  @Input() bottomBorder = false;
  @Input() data: Array<SourceStream>;

  columns: GovukTableColumn<SourceStream>[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-third' },
    { field: 'description', header: 'Description', widthClass: 'govuk-!-width-one-third' },
    { field: 'type', header: 'Type', widthClass: 'govuk-!-width-one-third' },
  ];
}
