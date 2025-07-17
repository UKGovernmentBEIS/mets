import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store';

import { NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { NonComplianceService } from '../core/non-compliance.service';

@Component({
  selector: 'app-conclusion',
  templateUrl: './conclusion.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConclusionComponent {
  payload$ = (
    this.nonComplianceService.getPayload() as Observable<NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload>
  ).pipe(
    first(),
    map((payload) => payload),
  );

  isAviation$ = this.authStore.pipe(
    selectCurrentDomain,
    map((v) => v === 'AVIATION'),
  );

  constructor(
    readonly nonComplianceService: NonComplianceService,
    public readonly authStore: AuthStore,
  ) {}
}
