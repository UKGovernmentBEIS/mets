import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { Bdrs2ActionService } from '@actions/bdrs2/core/bdrs2.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';

import { BDRS2ApplicationSubmittedRequestActionPayload } from 'pmrv-api';

interface ViewModel {
  overallDecision: OverallVerificationAssessment;
}

@Component({
  selector: 'app-overall-decision-submitted',
  imports: [ActionSharedModule, SharedModule],
  standalone: true,
  template: `
    @if (vm(); as vm) {
      <app-action-task header="Stage 2 BDR verification overall decision" [breadcrumb]="true">
        <app-shared-overall-decision-summary-template
          [isEditable]="false"
          [overallDecision]="vm.overallDecision"></app-shared-overall-decision-summary-template>
      </app-action-task>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2OverallDecisionSubmittedComponent {
  payload = this.bdrs2ActionService.payload as Signal<BDRS2ApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const overallDecision = this.payload().verificationReport?.overallAssessment as OverallVerificationAssessment;

    return { overallDecision };
  });

  constructor(private readonly bdrs2ActionService: Bdrs2ActionService) {}
}
