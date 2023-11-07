import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { DataGapsMethodologiesGroupComponent } from '@aviation/shared/components/aer-verify/data-gaps-methodologies-group/data-gaps-methodologies-group.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerDataGapsMethodologies, RequestActionDTO } from 'pmrv-api';

import { aerQuery } from '../../aer-ukets.selectors';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  dataGapsMethodologies: AviationAerDataGapsMethodologies;
}

@Component({
  selector: 'app-data-gaps-methodologies',
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-data-gaps-methodologies-group
        [dataGapsMethodologies]="vm.dataGapsMethodologies"
      ></app-data-gaps-methodologies-group>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, DataGapsMethodologiesGroupComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DataGapsMethodologiesComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['dataGapsMethodologies'],
        dataGapsMethodologies: {
          ...payload.verificationReport.dataGapsMethodologies,
        },
      };
    }),
  );
}
