import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { NerActionService } from '@actions/ner/core';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';

import { NERApplicationSubmittedRequestActionPayload } from 'pmrv-api';

interface ViewModel {
  overallDecision: OverallVerificationAssessment;
}

@Component({
  selector: 'app-action-ner-overall-decision',
  imports: [ActionSharedModule, SharedModule],
  template: `
    @let vm = this.vm();

    <app-action-task header="NER verification overall decision" [breadcrumb]="true">
      <app-shared-overall-decision-summary-template
        [overallDecision]="vm.overallDecision"
        [isEditable]="false"></app-shared-overall-decision-summary-template>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerOverallDecisionSubmittedComponent {
  private readonly nerActionService = inject(NerActionService);
  private readonly payload = this.nerActionService.payload as Signal<NERApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const overallDecision = this.payload().verificationReport?.overallAssessment as OverallVerificationAssessment;

    return {
      overallDecision,
    };
  });
}
