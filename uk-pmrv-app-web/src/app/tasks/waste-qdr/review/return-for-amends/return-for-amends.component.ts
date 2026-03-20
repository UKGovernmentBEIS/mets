import { ChangeDetectionStrategy, Component, computed, Signal, signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ChangesRequestedTemplateComponent } from '@shared/components/changes-requested-template/changes-requested-template.component';
import { SharedModule } from '@shared/shared.module';
import { WasteQdrService } from '@tasks/waste-qdr/core';
import { WasteQdrTaskComponent } from '@tasks/waste-qdr/shared';

import {
  WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload,
  WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails,
} from 'pmrv-api';

interface ViewModel {
  isSubmitted: boolean;
  isEditable: boolean;
  decisionDetails: WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails;
  regulatorReviewAttachments: { [key: string]: string };
  downloadUrl: string;
}

@Component({
  selector: 'app-waste-qdr-return-for-amends',
  imports: [ChangesRequestedTemplateComponent, SharedModule, WasteQdrTaskComponent],
  templateUrl: './return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrReturnForAmendsComponent {
  vm: Signal<ViewModel> = computed(() => {
    const isSubmitted = this.isSubmitted();
    const isEditable = this.isEditable();
    const {
      regulatorReviewAttachments,
      reviewDecision: { details },
    } = this.payload();

    return {
      isSubmitted,
      isEditable,
      regulatorReviewAttachments,
      decisionDetails: details as WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails,
      downloadUrl: this.wasteQdrService.getBaseFileDownloadUrl(),
    };
  });

  private readonly isEditable = this.wasteQdrService.isEditable;
  private readonly isSubmitted = signal(false);
  private readonly payload = this.wasteQdrService
    ?.payload as Signal<WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload>;
  private readonly requestTaskType = this.wasteQdrService.requestTaskType;

  constructor(
    private readonly wasteQdrService: WasteQdrService,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    this.wasteQdrService
      .postWasteQdrSubmit()
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.isSubmitted.set(true);
      });
  }
}
