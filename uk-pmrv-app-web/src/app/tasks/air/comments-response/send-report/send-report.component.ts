import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AirService } from '@tasks/air/shared/services/air.service';

@Component({
  selector: 'app-send-report',
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportComponent implements PendingRequest {
  reference = this.route.snapshot.paramMap.get('id');
  heading = `Item ${this.reference}: Send response to regulator`;
  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);
  isEditable$ = this.airService.isEditable$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backlinkService: BackLinkService,
  ) {}

  onConfirm() {
    this.airService
      .postSubmitRespondToRegulatorComments(this.reference)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.airService.requestId);
        this.isSubmitted$.next(true);
        this.backlinkService.hide();
      });
  }
}
