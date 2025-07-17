import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { ActivitySummaryTemplateComponent } from '@shared/components/alr/activity-summary-template/activity-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-activity-verifier-review',
  templateUrl: './activity-verifier-review.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule, ActivitySummaryTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityVerifierReviewComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  payload: Signal<ALRApplicationVerificationSubmitRequestTaskPayload> = this.alrService.payload;

  alr = computed(() => {
    const payload = this.payload() as ALRApplicationVerificationSubmitRequestTaskPayload;
    return payload.alr;
  });

  alrFile = computed(() => {
    const alr = this.alr();
    return alr?.alrFile ? this.alrService.getOperatorDownloadUrlAlrFile(alr.alrFile) : null;
  });

  files: Signal<AttachedFile[]> = computed(() => {
    const alr = this.alr();
    return alr?.files ? this.alrService.getOperatorDownloadUrlFiles(alr.files) : [];
  });

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
  ) {}
}
