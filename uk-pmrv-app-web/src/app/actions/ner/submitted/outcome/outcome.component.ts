import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { NerActionService } from '@actions/ner/core';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types';

import { NERApplicationCompletedRequestActionPayload, NERApplicationRegulatorReviewOutcome } from 'pmrv-api';

interface ViewModel {
  outcome: NERApplicationRegulatorReviewOutcome;
  nerFile: AttachedFile;
  supportingFiles: Array<AttachedFile>;
}

@Component({
  selector: 'app-ner-action-outcome',
  imports: [ActionSharedModule, SharedModule],
  templateUrl: './outcome.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerActionOutcomeComponent {
  private readonly nerActionService = inject(NerActionService);
  private readonly payload = this.nerActionService.payload as Signal<NERApplicationCompletedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const { regulatorReviewOutcome } = this.payload() ?? {};

    return {
      outcome: regulatorReviewOutcome,
      nerFile: this.nerActionService.getRegulatorDownloadUrlFile(regulatorReviewOutcome?.nerFile),
      supportingFiles: this.nerActionService.getRegulatorDownloadUrlFiles(regulatorReviewOutcome?.supportingFiles),
    };
  });
}
