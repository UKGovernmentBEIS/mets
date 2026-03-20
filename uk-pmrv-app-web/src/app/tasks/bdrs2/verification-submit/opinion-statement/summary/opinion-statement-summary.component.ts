import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-bdrs2-opinion-statement-summary',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  standalone: true,
  templateUrl: './opinion-statement-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OpinionStatementSummaryComponent {
  isEditable: Signal<boolean> = this.bdrs2Service.isEditable;
  bdrs2Payload: Signal<BDRS2ApplicationVerificationSubmitRequestTaskPayload> = this.bdrs2Service.payload;

  supportingFiles: Signal<AttachedFile[]> = computed(() => {
    const payload = this.bdrs2Payload();

    return payload?.verificationReport?.opinionStatement?.supportingFiles
      ? this.bdrs2Service.getVerifierDownloadUrlFiles(payload?.verificationReport?.opinionStatement?.supportingFiles)
      : [];
  });

  opinionStatementFile: Signal<AttachedFile> = computed(() => {
    const payload = this.bdrs2Payload();

    return payload?.verificationReport?.opinionStatement?.opinionStatementFile
      ? this.bdrs2Service.getVerifierDownloadUrlFile(
          payload?.verificationReport?.opinionStatement?.opinionStatementFile,
        )
      : ({} as AttachedFile);
  });

  hideSubmit: Signal<boolean> = computed(() => {
    const isEditable = this.isEditable();
    return !isEditable || this.bdrs2Payload().verificationSectionsCompleted?.['opinionStatement']?.[0];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.bdrs2Service
      .postVerificationTaskSave(null, true, 'opinionStatement')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
