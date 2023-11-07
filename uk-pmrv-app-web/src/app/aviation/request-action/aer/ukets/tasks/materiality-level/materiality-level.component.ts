import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { AerVerifyMaterialityLevelGroupComponent } from '@aviation/shared/components/aer-verify/materiality-level-group/materiality-level-group.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerMaterialityLevel, RequestActionDTO } from 'pmrv-api';

import { aerQuery } from '../../aer-ukets.selectors';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  materialityLevel: AviationAerMaterialityLevel;
}

@Component({
  selector: 'app-materiality-level',
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-aer-verify-materiality-level-group
        [materialityLevel]="vm.materialityLevel"
      ></app-aer-verify-materiality-level-group>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, AerVerifyMaterialityLevelGroupComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class MaterialityLevelComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['materialityLevel'],
        materialityLevel: {
          ...payload.verificationReport.materialityLevel,
        },
      };
    }),
  );
}
