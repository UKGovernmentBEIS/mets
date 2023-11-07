import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable } from 'rxjs';

import { FileInfoDTO, ReturnOfAllowancesApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';
import { ReturnOfAllowancesService } from '../core/return-of-allowances.service';

@Component({
  selector: 'app-submitted',
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  payload$ = (
    this.returnOfAllowancesService.getPayload() as Observable<ReturnOfAllowancesApplicationSubmittedRequestActionPayload>
  ).pipe(
    first(),
    map((payload) => payload.returnOfAllowances),
  );

  operators$ = this.store.pipe(
    map((state) =>
      Object.keys(state.action.payload['usersInfo']).filter(
        (userId) =>
          userId !==
          (state.action.payload as ReturnOfAllowancesApplicationSubmittedRequestActionPayload).decisionNotification
            .signatory,
      ),
    ),
  );

  signatory$ = this.store.pipe(
    map(
      (state) =>
        (state.action.payload as ReturnOfAllowancesApplicationSubmittedRequestActionPayload).decisionNotification
          .signatory,
    ),
  );

  officialNotice$: Observable<FileInfoDTO> = this.store.pipe(
    map((state) => state.action.payload['officialNotice']),
  ) as Observable<FileInfoDTO>;

  actionId$ = this.store.pipe(map((state) => state.action.id));

  constructor(readonly returnOfAllowancesService: ReturnOfAllowancesService, readonly store: CommonActionsStore) {}
}
