import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-action/aer/ukets/aer-ukets.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template';
import { SharedModule } from '@shared/shared.module';

import { AviationAerDataGaps, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  dataGaps: AviationAerDataGaps;
}

@Component({
  selector: 'app-data-gaps',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, DataGapsSummaryTemplateComponent, DataGapsListTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-data-gaps-summary-template [data]="vm.dataGaps"></app-data-gaps-summary-template>
      <ng-container *ngIf="vm.dataGaps.exist">
        <app-data-gaps-list-template [dataGaps]="vm.dataGaps.dataGaps"></app-data-gaps-list-template>
      </ng-container>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DataGapsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['dataGaps'],
      dataGaps: payload.aer.dataGaps,
    })),
  );
}
