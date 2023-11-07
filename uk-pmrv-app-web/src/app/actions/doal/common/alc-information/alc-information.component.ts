import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { RequestActionInfoDTO } from 'pmrv-api';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DoalActionService } from '../../core/doal-action.service';

@Component({
  selector: 'app-doal-alc-information',
  template: `
    <app-doal-action-task
      header="Provide information about this activity level change"
      [actionType]="requestActionType$ | async"
    >
      <app-doal-alc-information-template
        [data]="activityLevelChangeInformation$ | async"
        [editable]="false"
      ></app-doal-alc-information-template>
    </app-doal-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlcInformationComponent {
  activityLevelChangeInformation$ = this.doalActionService
    .getProceededPayload$()
    .pipe(map((payload) => payload.doal.activityLevelChangeInformation));

  requestActionType$: Observable<RequestActionInfoDTO['type']> = this.commonActionsStore.requestAction$.pipe(
    map((ra) => ra.type),
  );

  constructor(
    private readonly doalActionService: DoalActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
