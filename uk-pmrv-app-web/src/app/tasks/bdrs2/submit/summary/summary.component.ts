import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BDRS2BaselineSummaryTemplateComponent } from '@shared/components/bdrs2/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule, BDRS2BaselineSummaryTemplateComponent],
  standalone: true,
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BDRS2SummaryComponent {
  isEditable: Signal<boolean> = this.bdrs2Service.isEditable;
  bdrs2Payload: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.payload;
  returnLinkTitle = this.bdrs2Service.title();

  bdrs2 = computed(() => {
    const payload = this.bdrs2Payload();
    return payload.bdrs2;
  });

  bdrFiles: Signal<AttachedFile> = computed(() => {
    const payload = this.bdrs2Payload();
    return payload?.bdrs2?.bdrs2Files?.file
      ? this.bdrs2Service.getOperatorDownloadUrlBdrFile(payload?.bdrs2?.bdrs2Files?.file)
      : null;
  });

  files: Signal<AttachedFile[]> = computed(() => {
    const payload = this.bdrs2Payload();
    return payload?.bdrs2?.bdrs2Files?.supportingFiles
      ? this.bdrs2Service.getOperatorDownloadUrlFiles(payload?.bdrs2?.bdrs2Files?.supportingFiles)
      : [];
  });

  mmpFile: Signal<AttachedFile> = computed(() => {
    const payload = this.bdrs2Payload();
    return payload?.bdrs2?.mmpFiles?.file
      ? this.bdrs2Service.getOperatorDownloadUrlBdrFile(payload?.bdrs2?.mmpFiles?.file)
      : null;
  });

  mmpFiles: Signal<AttachedFile[]> = computed(() => {
    const payload = this.bdrs2Payload();
    return payload?.bdrs2?.mmpFiles?.supportingFiles
      ? this.bdrs2Service.getOperatorDownloadUrlFiles(payload?.bdrs2?.mmpFiles?.supportingFiles)
      : [];
  });

  hideSubmit: Signal<boolean> = computed(() => {
    const isEditable = this.isEditable();
    return !isEditable || this.bdrs2Payload().bdrs2SectionsCompleted?.['baseline'];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    const payload = this.bdrs2Payload();
    this.bdrs2Service
      .postTaskSave(
        {
          ...payload?.bdrs2,
        },
        {
          ...payload?.bdrs2Attachments,
        },
        true,
        'baseline',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }
}
