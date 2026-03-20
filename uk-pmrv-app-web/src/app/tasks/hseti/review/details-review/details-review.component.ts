import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { DetailsSummaryTemplateComponent } from '@shared/components/hseti';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { HseTiService } from '@tasks/hseti/core';
import { HsetiReviewGroupDecisionComponent } from '@tasks/hseti/shared/components/decision/hseti-review-group-decision/hseti-review-group-decision.component';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { HSETIApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-details-review',
  imports: [
    SharedModule,
    TaskSharedModule,
    HseTiTaskSharedModule,
    DetailsSummaryTemplateComponent,
    HsetiReviewGroupDecisionComponent,
  ],
  templateUrl: './details-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsReviewComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  payload: Signal<HSETIApplicationSubmitRequestTaskPayload> = this.hsetiService.payload;
  allocationPeriod: Signal<string> = this.hsetiService.allocationPeriod;

  hseti = computed(() => {
    const payload = this.payload() as HSETIApplicationSubmitRequestTaskPayload;
    return payload.hseti;
  });

  hsetiFile = computed(() => {
    const hseti = this.hseti();
    return hseti?.hsetiFile ? this.hsetiService.getOperatorDownloadUrlHsetiFile(hseti.hsetiFile) : null;
  });

  files: Signal<AttachedFile[]> = computed(() => {
    const hseti = this.hseti();
    return hseti?.files ? this.hsetiService.getOperatorDownloadUrlFiles(hseti.files) : [];
  });

  constructor(
    private readonly hsetiService: HseTiService,
    private readonly router: Router,
  ) {}
}
