import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store';

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

  isAviation$: Observable<boolean> = this.authStore.pipe(
    selectCurrentDomain,
    map((v) => v === 'AVIATION'),
  );

  constructor(public readonly authStore: AuthStore) {}
}
