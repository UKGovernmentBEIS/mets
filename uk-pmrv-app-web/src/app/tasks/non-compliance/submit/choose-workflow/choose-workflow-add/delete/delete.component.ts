import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../../../core/guards/pending-request.service';
import { NonComplianceService } from '../../../../core/non-compliance.service';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  index = this.route.snapshot.paramMap.get('index');

  constructor(
    readonly nonComplianceService: NonComplianceService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  delete(): void {
    this.nonComplianceService.payload$
      .pipe(
        first(),
        switchMap((payload) => {
          const nonCompliance = payload as NonComplianceApplicationSubmitRequestTaskPayload;
          return this.nonComplianceService.saveNonCompliance(
            {
              reason: nonCompliance.reason,
              nonComplianceDate: nonCompliance?.nonComplianceDate,
              complianceDate: nonCompliance?.complianceDate,
              comments: nonCompliance?.comments,
              selectedRequests: (payload as NonComplianceApplicationSubmitRequestTaskPayload)?.selectedRequests?.filter(
                (_, i) => i !== Number(this.index),
              ),
            },
            false,
          );
        }),
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
