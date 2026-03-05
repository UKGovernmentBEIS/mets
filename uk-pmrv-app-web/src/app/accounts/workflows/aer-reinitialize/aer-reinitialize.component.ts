import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { RequestsService } from 'pmrv-api';

import { createRequestCreateActionProcessDTO } from '../../../workflow-item/shared/workflow-related-create-actions/workflowCreateAction';

@Component({
  selector: 'app-aer-reinitialize',
  templateUrl: './aer-reinitialize.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerReinitializeComponent {
  isAerInitialized$ = new BehaviorSubject<boolean>(false);
  accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  requestId$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('request-id')));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly requestsService: RequestsService,
    private readonly route: ActivatedRoute,
  ) {}

  onInitialize(): void {
    combineLatest([this.requestId$, this.accountId$])
      .pipe(
        first(),
        switchMap(([requestId, accountId]) =>
          this.requestsService.processRequestCreateAction(
            createRequestCreateActionProcessDTO('AER', requestId),
            String(accountId),
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.isAerInitialized$.next(true));
  }
}
