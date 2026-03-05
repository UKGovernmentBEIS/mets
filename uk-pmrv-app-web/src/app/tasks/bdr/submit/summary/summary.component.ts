import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BaselineSummaryTemplateComponent } from '@shared/components/bdr/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, RouterLink, BaselineSummaryTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  isEditable: Signal<boolean> = this.bdrService.isEditable;
  bdrPayload: Signal<BDRApplicationSubmitRequestTaskPayload> = this.bdrService.payload;

  bdrFiles: Signal<AttachedFile> = computed(() => {
    const payload = this.bdrPayload();
    return payload?.bdr?.bdrFile ? this.bdrService.getOperatorDownloadUrlBdrFile(payload?.bdr?.bdrFile) : null;
  });

  files: Signal<AttachedFile[]> = computed(() => {
    const payload = this.bdrPayload();
    return payload?.bdr?.files ? this.bdrService.getOperatorDownloadUrlFiles(payload?.bdr?.files) : [];
  });
  mmpFiles: Signal<AttachedFile[]> = computed(() => {
    const payload = this.bdrPayload();
    return payload?.bdr?.mmpFiles ? this.bdrService.getOperatorDownloadUrlFiles(payload?.bdr?.mmpFiles) : [];
  });

  hideSubmit: Signal<boolean> = computed(() => {
    const isEditable = this.isEditable();
    return !isEditable || this.bdrPayload().bdrSectionsCompleted?.['baseline'];
  });

  backlink = computed(() => {
    return this.route.snapshot.data['backlink'] || '../../';
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    const payload = this.bdrPayload();
    this.bdrService
      .postTaskSave(
        {
          ...payload?.bdr,
        },
        {
          ...payload?.bdrAttachments,
        },
        true,
        'baseline',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }
}
