import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { VirService } from '@tasks/vir/core/vir.service';

@Component({
  selector: 'app-send-report',
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportComponent implements PendingRequest {
  reference = this.route.snapshot.data.reference;
  heading = `Submit response to ${this.reference}`;
  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);
  isEditable$ = this.virService.isEditable$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly virService: VirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backlinkService: BackLinkService,
  ) {}

  onConfirm() {
    this.virService
      .postSubmitRespondToRegulatorComments(this.reference)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.virService.requestId);
        this.isSubmitted$.next(true);
        this.backlinkService.hide();
      });
  }
}
