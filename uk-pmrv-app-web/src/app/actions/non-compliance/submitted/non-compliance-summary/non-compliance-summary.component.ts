import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { NonComplianceApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-non-compliance-summary',
  templateUrl: './non-compliance-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceSummaryComponent {
  nonCompliance$ = (
    this.nonComplianceService.getPayload() as Observable<NonComplianceApplicationSubmittedRequestActionPayload>
  ).pipe(map((payload) => payload));

  constructor(readonly nonComplianceService: NonComplianceService, readonly store: CommonActionsStore) {}
}
