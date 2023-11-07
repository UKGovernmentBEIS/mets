import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, shareReplay, switchMap } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { InstallationAccountOpeningDecisionRequestActionPayload, RequestActionsService } from 'pmrv-api';

@Component({
  selector: 'app-submitted-decision',
  templateUrl: './submitted-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedDecisionComponent implements OnInit {
  action$ = this.route.paramMap.pipe(
    switchMap((paramMap) => this.requestActionsService.getRequestActionById(Number(paramMap.get('actionId')))),
    map((requestAction) => ({
      ...requestAction,
      payload: requestAction.payload as InstallationAccountOpeningDecisionRequestActionPayload,
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
