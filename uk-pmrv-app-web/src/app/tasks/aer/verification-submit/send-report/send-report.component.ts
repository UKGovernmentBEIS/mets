import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BehaviorSubject, map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { verificationSubmitSendReportStatus } from '@tasks/aer/core/aer-task-statuses';

@Component({
  selector: 'app-send-report',
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportComponent implements PendingRequest {
  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);
  isSendReportAvailable$ = this.aerService
    .getPayload()
    .pipe(map((payload) => verificationSubmitSendReportStatus(payload) !== 'cannot start yet'));
  isEditable$ = this.aerService.isEditable$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly aerService: AerService,
    private readonly backlinkService: BackLinkService,
  ) {}

  onConfirm() {
    this.aerService
      .postSubmit('AER_SUBMIT_APPLICATION_VERIFICATION')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.aerService.requestId);
        this.isSubmitted$.next(true);
        this.backlinkService.hide();
      });
  }
}
