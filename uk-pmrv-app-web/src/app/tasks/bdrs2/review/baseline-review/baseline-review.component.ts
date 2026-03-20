import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { BDRS2BaselineSummaryTemplateComponent } from '@shared/components/bdrs2/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { BdrS2ReviewGroupDecisionComponent } from '@tasks/bdrs2/shared/components';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-baseline-review',
  imports: [
    SharedModule,
    TaskSharedModule,
    BdrS2TaskSharedModule,
    BDRS2BaselineSummaryTemplateComponent,
    BdrS2ReviewGroupDecisionComponent,
  ],
  templateUrl: './baseline-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineReviewComponent {
  notification = this.router.currentNavigation()?.extras.state?.notification;
  payload: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.payload;

  bdrs2 = computed(() => this.payload().bdrs2);

  bdrFile = computed(() => {
    const bdrs2 = this.bdrs2();
    return bdrs2?.bdrs2Files?.file ? this.bdrs2Service.getOperatorDownloadUrlBdrFile(bdrs2.bdrs2Files?.file) : null;
  });
  files: Signal<AttachedFile[]> = computed(() => {
    const bdrs2 = this.bdrs2();
    return bdrs2?.bdrs2Files?.supportingFiles
      ? this.bdrs2Service.getOperatorDownloadUrlFiles(bdrs2.bdrs2Files?.supportingFiles)
      : [];
  });

  mmpFile = computed(() => {
    const bdrs2 = this.bdrs2();
    return bdrs2?.mmpFiles?.file ? this.bdrs2Service.getOperatorDownloadUrlBdrFile(bdrs2.mmpFiles?.file) : null;
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
