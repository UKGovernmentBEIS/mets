import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { BdrActionService } from '@actions/bdr/core/bdr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { OutcomeSummaryTemplateComponent } from '@shared/components/bdr/outcome-summary-template/outcome-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { BDRApplicationCompletedRequestActionPayload } from 'pmrv-api';

export interface ViewModel {
  bdr: BDRApplicationCompletedRequestActionPayload['bdr'];
  outcome: BDRApplicationCompletedRequestActionPayload['regulatorReviewOutcome'];
  bdrFile: AttachedFile;
  files: AttachedFile[];
  isEditable: boolean;
}

@Component({
  selector: 'app-outcome-completed',
  standalone: true,
  imports: [ActionSharedModule, OutcomeSummaryTemplateComponent, NgIf, SharedModule],
  templateUrl: './outcome-completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutcomeCompletedComponent {
  payload = this.bdrActionService.payload as Signal<BDRApplicationCompletedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const outcome = this.payload().regulatorReviewOutcome;

    return {
      outcome,
      bdr: this.payload().bdr,
      bdrFile: outcome?.bdrFile ? this.bdrActionService.getRegulatorDownloadUrlFiles([outcome?.bdrFile])[0] : null,
      files: outcome?.files ? this.bdrActionService.getRegulatorDownloadUrlFiles(outcome?.files) : [],
      isEditable: false,
    };
  });

  constructor(private readonly bdrActionService: BdrActionService) {}
}
