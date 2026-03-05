import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable, switchMap } from 'rxjs';

import { RequestActionInfoDTO, RequestActionsService } from 'pmrv-api';

import { PermitTransferAService } from '../core/permit-transfer-a.service';

@Component({
  selector: 'app-transfer-wait',
  templateUrl: './wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferWaitComponent {
  action$: Observable<RequestActionInfoDTO> = this.permitTransferAService.requestId.pipe(
    first(),
    switchMap((requestId) => this.requestActionsService.getRequestActionsByRequestId(requestId)),
    map((actions) => actions[0]),
  );
  submitter$ = this.action$.pipe(map((action) => action.submitter));

  constructor(
    readonly permitTransferAService: PermitTransferAService,
    private readonly requestActionsService: RequestActionsService,
  ) {}
}
