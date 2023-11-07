import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable } from 'rxjs';

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

  constructor(readonly nonComplianceService: NonComplianceService) {}
}
