import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { BdrActionService } from '@actions/bdr/core/bdr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { OutcomeSummaryTemplateComponent } from '@shared/components/bdrs2/outcome-summary-template/outcome-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { BDRS2ApplicationCompletedRequestActionPayload } from 'pmrv-api';

export interface ViewModel {
  bdrs2: BDRS2ApplicationCompletedRequestActionPayload['bdrs2'];
  outcome: BDRS2ApplicationCompletedRequestActionPayload['regulatorReviewOutcome'];
  bdrFile: AttachedFile;
  files: AttachedFile[];
  isEditable: boolean;
}

@Component({
  selector: 'app-outcome-completed',
  imports: [ActionSharedModule, OutcomeSummaryTemplateComponent, SharedModule],
  templateUrl: './outcome-completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutcomeCompletedComponent {
  payload = this.bdrActionService.payload as Signal<BDRS2ApplicationCompletedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const outcome = this.payload().regulatorReviewOutcome;

    return {
      outcome,
      bdrs2: this.payload().bdrs2,
      bdrFile: outcome?.file ? this.bdrActionService.getRegulatorDownloadUrlFiles([outcome?.file])[0] : null,
      files: outcome?.supportingFiles
        ? this.bdrActionService.getRegulatorDownloadUrlFiles(outcome?.supportingFiles)
        : [],
      isEditable: false,
    };
  });

  constructor(private readonly bdrActionService: BdrActionService) {}
}
