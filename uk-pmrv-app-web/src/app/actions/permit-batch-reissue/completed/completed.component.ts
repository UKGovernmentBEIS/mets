import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { BatchReissueCompletedRequestActionPayload } from 'pmrv-api';

import { FiltersModel } from '../../../shared/components/permit-batch-reissue/filters.model';
import { CommonActionsStore } from '../../store/common-actions.store';

@Component({
  selector: 'app-completed',
  templateUrl: './completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompletedComponent {
  action$ = this.store.pipe(map((store) => store.action));
  payload$ = this.action$.pipe(map((action) => action.payload as BatchReissueCompletedRequestActionPayload));

  filters$ = this.payload$.pipe(
    map((payload) => {
      return {
        ...payload.filters,
        numberOfEmitters: payload.numberOfAccounts,
      } as FiltersModel;
    }),
  );

  constructor(private readonly store: CommonActionsStore) {}
}
