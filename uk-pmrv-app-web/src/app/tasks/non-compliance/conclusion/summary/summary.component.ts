import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { NonComplianceFinalDeterminationRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras?.state?.notification;
  isEditable$ = this.nonComplianceService.isEditable$;
  payload$ = this.nonComplianceService.getPayload().pipe(
    first(),
    map((payload) => payload as NonComplianceFinalDeterminationRequestTaskPayload),
  );

  requestTask$ = this.nonComplianceService.requestTaskItem$.pipe(
    first(),
    map((requestTaskItem) => requestTaskItem.requestTask),
  );

  returnTo: { text: string; link: string };

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly nonComplianceService: NonComplianceService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.nonComplianceService
      .saveConclusionSectionStatus(true)
      .pipe(switchMap(() => this.nonComplianceService.submitConclusion().pipe(this.pendingRequest.trackRequest())))
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }
}
