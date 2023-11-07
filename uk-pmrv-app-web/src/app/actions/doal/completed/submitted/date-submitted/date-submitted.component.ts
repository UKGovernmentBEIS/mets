import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { CommonActionsStore } from '../../../../store/common-actions.store';
import { DoalActionService } from '../../../core/doal-action.service';

@Component({
  selector: 'app-doal-date-submitted',
  template: `
    <app-doal-action-task
      header="Provide the date application was submitted to UK authorities"
      [actionType]="actionType$ | async"
    >
      <app-doal-date-submitted-summary-template
        [dateSubmittedToAuthority]="dateSubmittedToAuthority$ | async"
        [editable]="false"
      ></app-doal-date-submitted-summary-template>
    </app-doal-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DateSubmittedComponent {
  actionType$ = this.commonActionsStore.requestActionType$;
  dateSubmittedToAuthority$ = this.doalActionService
    .getCompletedPayload$()
    .pipe(map((payload) => payload.doalAuthority.dateSubmittedToAuthority));

  constructor(
    private readonly doalActionService: DoalActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
