import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { ReissueCompletedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Component({
  selector: 'app-completed',
  templateUrl: './completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompletedComponent {
  action$ = this.store.pipe(map((store) => store.action));
  payload$ = this.action$.pipe(map((action) => action.payload as ReissueCompletedRequestActionPayload));

  constructor(private readonly store: CommonActionsStore) {}
}
