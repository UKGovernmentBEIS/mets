import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { NonComplianceFinalDeterminationRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

import { NonComplianceService } from '../core/non-compliance.service';
import { resolveSectionStatus } from './section.status';

@Component({
  selector: 'app-conclusion',
  templateUrl: './conclusion.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConclusionComponent {
  sectionStatus$ = this.nonComplianceService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as NonComplianceFinalDeterminationRequestTaskPayload)),
  );

  expectedType: RequestTaskDTO['type'] = this.router.url.includes('/aviation/')
    ? 'AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION'
    : 'NON_COMPLIANCE_FINAL_DETERMINATION';

  constructor(
    private readonly nonComplianceService: NonComplianceService,
    readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}
}
