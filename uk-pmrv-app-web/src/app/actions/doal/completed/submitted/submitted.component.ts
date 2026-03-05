import { ChangeDetectionStrategy, Component } from '@angular/core';

import { CommonActionsStore } from '../../../store/common-actions.store';

@Component({
  selector: 'app-doal-submitted',
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  actionType$ = this.commonActionsStore.requestActionType$;

  constructor(private readonly commonActionsStore: CommonActionsStore) {}
}
