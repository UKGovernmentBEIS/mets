import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, shareReplay, switchMap } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { AerApplicationMarkNotRequiredRequestActionPayload, RequestActionsService } from 'pmrv-api';

@Component({
  selector: 'app-aer-not-required-details',
  templateUrl: './aer-mark-as-not-required-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerMarkAsNotRequiredDetailsComponent implements OnInit {
  action$ = this.route.paramMap.pipe(
    switchMap((paramMap) => this.requestActionsService.getRequestActionById(Number(paramMap.get('actionId')))),
    map((requestAction) => ({
      ...requestAction,
      payload: requestAction.payload as AerApplicationMarkNotRequiredRequestActionPayload,
    })),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    private readonly requestActionsService: RequestActionsService,
    private readonly backLinkService: BackLinkService,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
