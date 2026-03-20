import { ChangeDetectionStrategy, Component, computed, Signal, signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { WasteQdrService } from '@tasks/waste-qdr/core';

import { RequestInfoDTO, RequestTaskDTO } from 'pmrv-api';

import { WasteQdrReturnLinkComponent } from '../waste-qdr-return-link/waste-qdr-return-link.component';

interface ViewModel {
  isEditable: boolean;
  requestId: RequestInfoDTO['id'];
  requestTaskType: RequestTaskDTO['type'];
  isSubmitted: boolean;
}

@Component({
  selector: 'app-send-report',
  imports: [SharedModule, WasteQdrReturnLinkComponent],
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrSendReportComponent {
  isEditable = this.wasteQdrService.isEditable;
  requestId = this.wasteQdrService.requestId;
  isSubmitted = signal(false);
  requestTaskType = this.wasteQdrService.requestTaskType;

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const isSubmitted = this.isSubmitted();
    const requestId = this.requestId;
    const requestTaskType = this.requestTaskType();

    return {
      isEditable,
      isSubmitted,
      requestId,
      requestTaskType,
    };
  });

  constructor(
    readonly wasteQdrService: WasteQdrService,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    this.wasteQdrService
      .postWasteQdrSubmit()
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.isSubmitted.set(true));
  }
}
