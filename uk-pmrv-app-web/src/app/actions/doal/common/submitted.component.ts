import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { RequestActionInfoDTO } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Component({
  selector: 'app-doal-submitted',
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  constructor(private readonly commonActionsStore: CommonActionsStore) {}

  requestActionType$: Observable<RequestActionInfoDTO['type']> = this.commonActionsStore.requestAction$.pipe(
    map((ra) => ra.type),
  );

  requestActionPayload$: Observable<any> = this.commonActionsStore.requestAction$.pipe(map((ra) => ra.payload));
}
