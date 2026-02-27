import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, shareReplay, switchMap } from 'rxjs';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { AlrApplicationMarkNotRequiredRequestActionPayload, RequestActionsService } from 'pmrv-api';

@Component({
  selector: 'app-alr-not-required-details',
  imports: [ActionSharedModule, SharedModule],
  templateUrl: './marked-as-not-required.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrMarkedAsNotRequiredDetailsComponent {
  action$ = this.route.paramMap.pipe(
    switchMap((paramMap) => this.requestActionsService.getRequestActionById(Number(paramMap.get('actionId')))),
    map((requestAction) => ({
      ...requestAction,
      payload: requestAction.payload as AlrApplicationMarkNotRequiredRequestActionPayload,
    })),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    private readonly requestActionsService: RequestActionsService,
    private readonly route: ActivatedRoute,
  ) {}
}
