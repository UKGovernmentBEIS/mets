import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { calculateAffectedFlightsDataGaps } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps.util';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaDataGaps, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  dataGaps: AviationAerCorsiaDataGaps;
  affectedFlightsDataGaps: number;
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
      <app-data-gaps-summary-template
        [data]="vm.dataGaps"
        [affectedFlightsDataGaps]="vm.affectedFlightsDataGaps"
      ></app-data-gaps-summary-template>
      <app-data-gaps-list-template
        *ngIf="vm.dataGaps?.dataGapsDetails?.dataGaps"
        [dataGaps]="vm.dataGaps.dataGapsDetails.dataGaps"
      ></app-data-gaps-list-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DataGapsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectAer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([aer, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['dataGaps'],
      dataGaps: aer.dataGaps,
      affectedFlightsDataGaps: calculateAffectedFlightsDataGaps(aer.dataGaps?.dataGapsDetails?.dataGaps ?? []),
    })),
  );
}
