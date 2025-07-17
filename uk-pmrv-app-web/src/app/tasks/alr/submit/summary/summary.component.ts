import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ActivitySummaryTemplateComponent } from '@shared/components/alr/activity-summary-template/activity-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, RouterLink, AlrTaskSharedModule, ActivitySummaryTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  isEditable: Signal<boolean> = this.alrService.isEditable;
  alrPayload: Signal<ALRApplicationSubmitRequestTaskPayload> = this.alrService.payload;

  alrFiles: Signal<AttachedFile> = computed(() => {
    const payload = this.alrPayload();
    return payload?.alr?.alrFile ? this.alrService.getOperatorDownloadUrlAlrFile(payload?.alr?.alrFile) : null;
  });

  files: Signal<AttachedFile[]> = computed(() => {
    const payload = this.alrPayload();
    return payload?.alr?.files ? this.alrService.getOperatorDownloadUrlFiles(payload?.alr?.files) : [];
  });

  hideSubmit: Signal<boolean> = computed(() => {
    const isEditable = this.isEditable();
    return !isEditable || this.alrPayload().alrSectionsCompleted?.['activity'];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    const payload = this.alrPayload();
    this.alrService
      .postTaskSave(
        {
          ...payload?.alr,
        },
        {
          ...payload?.alrAttachments,
        },
        true,
        'activity',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }
}
