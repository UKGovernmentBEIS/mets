import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { ActivitySummaryTemplateComponent } from '@shared/components/alr';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-alr-activity-review',
  templateUrl: './activity-review.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule, ActivitySummaryTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityReviewComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  alr = computed(() => {
    const payload = this.payload();
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
