import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';

import { BDRS2ApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { submitVerificationWizardComplete } from '../verification.wizard';

@Component({
  selector: 'app-send-bdrs2-report',
  imports: [SharedModule, BdrS2TaskSharedModule],
  standalone: true,
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendBdrs2ReportComponent implements PendingRequest {
  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);

  payload = this.bdrs2Service.payload;
  isSendReportAvailable = computed(() => {
    return submitVerificationWizardComplete(this.payload() as BDRS2ApplicationVerificationSubmitRequestTaskPayload);
  });
  isEditable: Signal<boolean> = this.bdrs2Service.isEditable;

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly bdrs2Service: BdrS2Service,
  ) {}

  onConfirm() {
    this.bdrs2Service
      .postSubmit('BDRS2_SUBMIT_VERIFICATION')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.bdrs2Service.requestId);
        this.isSubmitted$.next(true);
      });
  }
}
