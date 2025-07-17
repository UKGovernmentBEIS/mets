import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { AlrActionService } from '@actions/alr/core/alr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { ActivitySummaryTemplateComponent } from '@shared/components/alr/activity-summary-template/activity-summary-template.component';
import { AttachedFile } from '@shared/types/attached-file.type';

import { ALR, ALRApplicationSubmittedRequestActionPayload } from 'pmrv-api';

interface ViewModel {
  alr: ALR;
  alrFiles: AttachedFile;
  files: AttachedFile[];
}

@Component({
  selector: 'app-alr-action-activity-submitted',
  standalone: true,
  imports: [ActionSharedModule, NgIf, ActivitySummaryTemplateComponent],
  templateUrl: './activity-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrActivitySubmittedComponent {
  payload = this.alrActionService.payload as Signal<ALRApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();

    return {
      alr: payload.alr,
      alrFiles: this.alrActionService.getOperatorDownloadUrlAlrFile(payload?.alr?.alrFile),
      files: this.alrActionService.getOperatorDownloadUrlFiles(payload?.alr?.files),
    };
  });

  constructor(private readonly alrActionService: AlrActionService) {}
}
