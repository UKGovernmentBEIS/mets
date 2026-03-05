import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { DoalGrantAuthorityResponse } from 'pmrv-api';

import { CommonActionsStore } from '../../../../store/common-actions.store';
import { DoalActionService } from '../../../core/doal-action.service';

@Component({
  selector: 'app-doal-response',
  template: `
    <app-doal-action-task header="Provide UK ETS Authority response" [actionType]="actionType$ | async">
      <app-doal-authority-decision-template
        [data]="authorityResponse$ | async"
        [documents]="documentFiles$ | async"
        [editable]="false"></app-doal-authority-decision-template>
    </app-doal-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResponseComponent {
  actionType$ = this.commonActionsStore.requestActionType$;
  authorityResponse$ = this.doalActionService
    .getCompletedPayload$()
    .pipe(map((payload) => payload.doalAuthority.authorityResponse));
  documentFiles$ = this.authorityResponse$.pipe(
    map((authorityResponse) => (authorityResponse as DoalGrantAuthorityResponse)?.documents ?? []),
    map((files) => this.doalActionService.getDownloadUrlFiles(files)),
  );

  constructor(
    private readonly doalActionService: DoalActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
