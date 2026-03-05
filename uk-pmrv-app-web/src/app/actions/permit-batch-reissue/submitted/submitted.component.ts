import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { BatchReissueSubmittedRequestActionPayload } from 'pmrv-api';

import { FiltersModel } from '../../../shared/components/permit-batch-reissue/filters.model';
import { CommonActionsStore } from '../../store/common-actions.store';

@Component({
  selector: 'app-submitted',
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  payload$ = this.store.pipe(map((store) => store.action.payload as BatchReissueSubmittedRequestActionPayload));

  filters$ = this.payload$.pipe(
    map((payload) => {
      return {
        ...payload.filters,
        numberOfEmitters: null,
      } as FiltersModel;
    }),
  );

  constructor(private readonly store: CommonActionsStore) {}
}
