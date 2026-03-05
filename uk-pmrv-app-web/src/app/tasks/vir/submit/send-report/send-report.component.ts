import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { BehaviorSubject, map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { VirService } from '@tasks/vir/core/vir.service';
import { submitWizardComplete } from '@tasks/vir/submit/submit.wizard';

@Component({
  selector: 'app-send-report',
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportComponent implements PendingRequest {
  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);
  isSendReportAvailable$ = this.virService.payload$.pipe(map((payload) => submitWizardComplete(payload)));
  isEditable$ = this.virService.isEditable$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly virService: VirService,
    private readonly router: Router,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly backlinkService: BackLinkService,
  ) {}

  onConfirm() {
    this.virService
      .postSubmit()
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.virService.requestId);
        this.breadcrumbService.showDashboardBreadcrumb(this.router.url);
        this.isSubmitted$.next(true);
        this.backlinkService.hide();
      });
  }
}
