import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { RequestsService } from 'pmrv-api';

@Component({
  selector: 'app-trigger-air',
  templateUrl: './trigger-air.component.html',

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TriggerAirComponent {
  isAirInitialized$ = new BehaviorSubject<boolean>(false);
  accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly requestsService: RequestsService,
    private readonly route: ActivatedRoute,
  ) {}

  onInitialize(): void {
    this.accountId$
      .pipe(
        first(),
        switchMap((accountId) =>
          this.requestsService.processRequestCreateAction(
            {
              requestCreateActionType: 'AIR',
              requestCreateActionPayload: {
                payloadType: 'EMPTY_PAYLOAD',
              },
            },
            String(accountId),
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((processResponse) => {
        this.requestId$.next(processResponse.requestId);
        this.isAirInitialized$.next(true);
      });
  }
}
