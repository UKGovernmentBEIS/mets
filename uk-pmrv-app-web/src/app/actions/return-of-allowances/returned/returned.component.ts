import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable } from 'rxjs';

import { ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { ReturnOfAllowancesService } from '../core/return-of-allowances.service';

@Component({
  selector: 'app-returned',
  templateUrl: './returned.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnedComponent {
  payload$ = (
    this.returnOfAllowancesService.getPayload() as Observable<ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload>
  ).pipe(
    first(),
    map((payload) => payload.returnOfAllowancesReturned),
  );

  constructor(readonly returnOfAllowancesService: ReturnOfAllowancesService) {}
}
