import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { ItemDTO } from 'pmrv-api';

@Component({
  selector: 'app-workflow-items-list',
  templateUrl: './workflow-items-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowItemsListComponent {
  @Input() items: ItemDTO[];
  @Input() tableColumns: GovukTableColumn<ItemDTO>[];
  @Input() unassignedLabel: string;
}
