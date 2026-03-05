import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { DoalProceedToAuthorityDetermination, RequestActionInfoDTO } from 'pmrv-api';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DoalActionService } from '../../core/doal-action.service';

@Component({
  selector: 'app-doal-determination',
  templateUrl: './determination.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationComponent {
  determination$ = this.doalActionService
    .getProceededPayload$()
    .pipe(map((payload) => payload.doal.determination as DoalProceedToAuthorityDetermination));

  requestActionType$: Observable<RequestActionInfoDTO['type']> = this.commonActionsStore.requestAction$.pipe(
    map((ra) => ra.type),
  );

  constructor(
    private readonly doalActionService: DoalActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
