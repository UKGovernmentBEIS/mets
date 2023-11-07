import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AircraftTypesDataTableComponent } from '@aviation/shared/components/aer/aircraft-types-table/aircraft-types-data-table.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerAircraftDataDetails, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  aviationAerAircraftDataDetails: AviationAerAircraftDataDetails[];
}

@Component({
  selector: 'app-aircraft-types-data',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, AircraftTypesDataTableComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-aircraft-types-data-table
        [headingText]="'File uploaded'"
        [aviationAerAircraftDataDetails]="vm.aviationAerAircraftDataDetails"
      ></app-aircraft-types-data-table>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AircraftTypesDataComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['aviationAerAircraftData'],
      aviationAerAircraftDataDetails: payload.aer.aviationAerAircraftData.aviationAerAircraftDataDetails,
    })),
  );
}
