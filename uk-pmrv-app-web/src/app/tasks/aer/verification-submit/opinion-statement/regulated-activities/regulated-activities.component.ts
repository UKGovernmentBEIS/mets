import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';

import { OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-regulated-activities',
  templateUrl: './regulated-activities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatedActivitiesComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;
  regulatedActivities$ = this.aerService.getMappedPayload<OpinionStatement['regulatedActivities']>([
    'verificationReport',
    'opinionStatement',
    'regulatedActivities',
  ]);

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
      .subscribe(() => this.router.navigate(['combustion-sources'], { relativeTo: this.route }));
  }
}
