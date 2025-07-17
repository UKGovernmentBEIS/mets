import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BdrService, BdrTaskSharedModule } from '@tasks/bdr/shared';

import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { submitVerificationWizardComplete } from '../verification.wizard';

@Component({
  selector: 'app-send-bdr-report',
  templateUrl: './send-report.component.html',
  standalone: true,
  imports: [SharedModule, BdrTaskSharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendBdrReportComponent implements PendingRequest {
  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);

  isSendReportAvailable: Signal<boolean> = computed(() => {
    const payload = this.bdrService.payload();
    return submitVerificationWizardComplete(payload as BDRApplicationVerificationSubmitRequestTaskPayload);
  });
  isEditable: Signal<boolean> = this.bdrService.isEditable;

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly bdrService: BdrService,
  ) {}

  onConfirm() {
    this.bdrService
      .postSubmit('BDR_SUBMIT_VERIFICATION')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.bdrService.requestId);
        this.isSubmitted$.next(true);
      });
  }
}
