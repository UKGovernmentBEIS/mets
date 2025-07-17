import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map } from 'rxjs';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { NonComplianceService } from '../core/non-compliance.service';
import { resolveSectionStatus } from './section.status';

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  sectionStatus$ = this.nonComplianceService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as NonComplianceApplicationSubmitRequestTaskPayload)),
  );

  canViewSectionDetails$ = combineLatest([this.nonComplianceService.isEditable$, this.sectionStatus$]).pipe(
    map(([isEditable, sectionStatus]) => isEditable || sectionStatus !== 'not started'),
  );

  constructor(
    public readonly nonComplianceService: NonComplianceService,
    readonly route: ActivatedRoute,
  ) {}
}
