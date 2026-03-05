import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SummaryTemplateComponent } from '@shared/components/waste-qdr/summary-template/summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { WasteQdrService } from '@tasks/waste-qdr/core';
import { WasteQdrReviewGroupDecisionComponent } from '@tasks/waste-qdr/shared';
import { WasteQdrTaskReviewComponent } from '@tasks/waste-qdr/shared/components/waste-qdr-task-review/waste-qdr-task-review.component';

import { WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

interface ViewModel {
  header: string;
  qdrReport: AttachedFile | null;
  supportingFiles: AttachedFile[];
  reportProvided: boolean;
  notes?: string;
  reasonForUnprovided?: string;
  requestMetadata: any;
}
@Component({
  selector: 'app-qdr-review',
  standalone: true,
  imports: [
    SharedModule,
    TaskSharedModule,
    WasteQdrTaskReviewComponent,
    WasteQdrReviewGroupDecisionComponent,
    SummaryTemplateComponent,
  ],
  templateUrl: './qdr-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class QdrReviewComponent {
  requestMetadata = this.wasteQdrService.requestMetadata;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  payload = this.wasteQdrService?.payload as Signal<WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload>;
  reportProvided = computed(() => this.payload().qdr.reportProvided);

  vm: Signal<ViewModel> = computed(() => {
    const header = 'Quarterly data report submitted to regulator';
    const wasteQdr = this.payload().qdr;
    const reportProvided = this.reportProvided();
    const requestMetadata = this.requestMetadata();

    return {
      header,
      qdrReport:
        wasteQdr?.reportProvided && wasteQdr.report
          ? this.wasteQdrService.getDownloadUrlFiles([wasteQdr?.report])[0]
          : null,
      supportingFiles: wasteQdr?.supportingFiles
        ? this.wasteQdrService.getDownloadUrlFiles(wasteQdr?.supportingFiles)
        : [],
      reportProvided,
      notes: wasteQdr?.notes,
      reasonForUnprovided: wasteQdr?.reasonForUnprovided,
      requestMetadata,
    };
  });

  constructor(
    private readonly wasteQdrService: WasteQdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}
}
