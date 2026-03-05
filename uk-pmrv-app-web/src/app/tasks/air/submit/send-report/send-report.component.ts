import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BehaviorSubject, map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AirService } from '@tasks/air/shared/services/air.service';
import { submitWizardComplete } from '@tasks/air/submit/submit.wizard';

import { AirRequestMetadata } from 'pmrv-api';

@Component({
  selector: 'app-send-report',
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportComponent implements PendingRequest {
  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);
  isSendReportAvailable$ = this.airService.payload$.pipe(map((payload) => submitWizardComplete(payload)));
  isEditable$ = this.airService.isEditable$;
  title$ = this.airService.requestMetadata$.pipe(
    map((metadata) => {
      const year = (metadata as AirRequestMetadata).year;
      return `Are you sure you want to submit your ${year} Annual improvement report to your regulator?`;
    }),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly backlinkService: BackLinkService,
  ) {}

  onSubmit() {
    this.airService
      .postSubmit()
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.airService.requestId);
        this.isSubmitted$.next(true);
        this.backlinkService.hide();
      });
  }
}
