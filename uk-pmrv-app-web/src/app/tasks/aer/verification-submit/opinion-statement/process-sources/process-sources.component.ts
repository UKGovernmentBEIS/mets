import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-process-sources',
  templateUrl: './process-sources.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessSourcesComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;
  processSources$ = (
    this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
  ).pipe(map((payload) => payload.verificationReport.opinionStatement.processSources));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    this.aerService
      .postVerificationTaskSave(null, false, 'opinionStatement')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../monitoring-approaches'], { relativeTo: this.route }));
  }
}
