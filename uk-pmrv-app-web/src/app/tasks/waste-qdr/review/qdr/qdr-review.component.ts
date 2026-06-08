import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ReviewGroupDecisionSharedComponent } from '@shared/components/review-group-decision/review-group-decision.component';
import { constructReviewDecision } from '@shared/components/review-group-decision/review-group-decision.utils';
import { SummaryTemplateComponent } from '@shared/components/waste-qdr/summary-template/summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { WasteQdrService } from '@tasks/waste-qdr/core';
import { WasteQdrTaskReviewComponent } from '@tasks/waste-qdr/shared/components/waste-qdr-task-review/waste-qdr-task-review.component';

import { WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

interface ViewModel {
  header: string;
  qdrReport: AttachedFile | null;
  supportingFiles: AttachedFile[];
  reportProvided: boolean;
  requestMetadata: any;

  isEditable: boolean;
  requestTaskId: number;
  payload: WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;
  downloadUrl: string;
}
@Component({
  selector: 'app-qdr-review',
  imports: [
    SharedModule,
    TaskSharedModule,
    WasteQdrTaskReviewComponent,
    ReviewGroupDecisionSharedComponent,
    SummaryTemplateComponent,
  ],
  templateUrl: './qdr-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class QdrReviewComponent {
  private readonly isEditable = this.wasteQdrService.isEditable;
  private readonly requestMetadata = this.wasteQdrService.requestMetadata;
  private readonly payload = this.wasteQdrService
    ?.payload as Signal<WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload>;
  private readonly reportProvided = computed(() => this.payload().qdr.reportProvided);

  vm: Signal<ViewModel> = computed(() => {
    const header = 'Quarterly data report submitted to regulator';
    const payload = this.payload();
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
      requestMetadata,
      isEditable: this.isEditable(),
      requestTaskId: this.wasteQdrService.requestTaskId,
      payload,
      downloadUrl: this.wasteQdrService.getBaseFileDownloadUrl(),
    };
  });

  constructor(
    private readonly wasteQdrService: WasteQdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(form: UntypedFormGroup) {
    form.removeControl('verificationRequired');

    this.wasteQdrService
      .postDecisionReview(
        constructReviewDecision(form),
        'qdr',
        form.controls.requiredChanges.value.map((requiredChange: any) => requiredChange.files).flat(),
      )
      .subscribe(() => {
        this.router.navigate(['../'], { relativeTo: this.route });
      });
  }
}
