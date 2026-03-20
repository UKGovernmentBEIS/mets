import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { BDRS2BaselineSummaryTemplateComponent } from '@shared/components/bdrs2/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-baseline-verifier-review',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule, BDRS2BaselineSummaryTemplateComponent],
  standalone: true,
  templateUrl: './baseline-verifier-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineVerifierReviewComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  payload: Signal<BDRS2ApplicationVerificationSubmitRequestTaskPayload> = this.bdrs2Service.payload;

  bdrs2 = computed(() => {
    const payload = this.payload();
    return payload.bdrs2;
  });

  bdrs2File = computed(() => {
    const bdrs2 = this.bdrs2();
    return bdrs2?.bdrs2Files?.file ? this.bdrs2Service.getOperatorDownloadUrlBdrFile(bdrs2.bdrs2Files?.file) : null;
  });

  files: Signal<AttachedFile[]> = computed(() => {
    const bdrs2 = this.bdrs2();
    return bdrs2?.bdrs2Files?.supportingFiles
      ? this.bdrs2Service.getOperatorDownloadUrlFiles(bdrs2.bdrs2Files?.supportingFiles)
      : [];
  });
  mmpFile: Signal<AttachedFile> = computed(() => {
    const bdrs2 = this.bdrs2();
    return bdrs2?.mmpFiles?.file ? this.bdrs2Service.getOperatorDownloadUrlFiles([bdrs2.mmpFiles?.file])[0] : null;
  });
  mmpFiles: Signal<AttachedFile[]> = computed(() => {
    const bdrs2 = this.bdrs2();
    return bdrs2?.mmpFiles?.supportingFiles
      ? this.bdrs2Service.getOperatorDownloadUrlFiles(bdrs2.mmpFiles?.supportingFiles)
      : [];
  });

  constructor(
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
  ) {}
}
