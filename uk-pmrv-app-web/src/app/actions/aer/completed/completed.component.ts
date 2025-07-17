import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, filter, map, Observable, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@core/store/auth';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../core/aer.service';

@Component({
  selector: 'app-aer-completed',
  templateUrl: './completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class CompletedComponent {
  payload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
  requestAction$ = this.aerService.requestAction$;

  aerTitle$ = combineLatest([this.requestAction$, this.payload$]).pipe(
    map(([requestAction, payload]) => {
      return requestAction?.type === 'AER_APPLICATION_REVIEW_SKIPPED'
        ? payload.reportingYear + ' completed without review'
        : payload.reportingYear + ' emissions report reviewed';
    }),
  );

  aerLink$ = this.authStore.pipe(
    selectUserRoleType,
    map((roleType) => (roleType === 'REGULATOR' ? '../reviewed' : '../submitted')),
    takeUntil(this.destroy$),
  );

  isRegulator$ = this.authStore.pipe(
    filter((roleType) => roleType.userState.roleType === 'REGULATOR'),
    takeUntil(this.destroy$),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}
}
