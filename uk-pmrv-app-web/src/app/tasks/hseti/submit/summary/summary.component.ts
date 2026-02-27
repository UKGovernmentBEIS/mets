import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DetailsSummaryTemplateComponent } from '@shared/components/hseti/details-summary-template/details-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { HseTiService } from '@tasks/hseti/core';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { HSETIApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule, DetailsSummaryTemplateComponent],
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  readonly isEditable: Signal<boolean> = this.hseTiService.isEditable;
  readonly hsetiPayload: Signal<HSETIApplicationSubmitRequestTaskPayload> = this.hseTiService.payload;
  readonly allocationPeriod: Signal<string> = this.hseTiService.allocationPeriod;

  readonly returnLink: Signal<string> = computed(
    () => `Complete ${this.allocationPeriod()} HSE target increase application`,
  );

  readonly hsetiFile: Signal<AttachedFile> = computed(() => {
    const payload = this.hsetiPayload();
    return payload?.hseti?.hsetiFile
      ? this.hseTiService.getOperatorDownloadUrlHsetiFile(payload?.hseti?.hsetiFile)
      : null;
  });

  readonly files: Signal<AttachedFile[]> = computed(() => {
    const payload = this.hsetiPayload();
    return payload?.hseti?.files ? this.hseTiService.getOperatorDownloadUrlFiles(payload?.hseti?.files) : [];
  });

  readonly hideSubmit: Signal<boolean> = computed(() => {
    const isEditable = this.isEditable();
    return !isEditable || this.hsetiPayload().hsetiSectionsCompleted?.['details'];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly hseTiService: HseTiService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    const payload = this.hsetiPayload();
    this.hseTiService
      .postTaskSave(
        {
          ...payload?.hseti,
        },
        {
          ...payload?.hsetiAttachments,
        },
        true,
        'details',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }
}
