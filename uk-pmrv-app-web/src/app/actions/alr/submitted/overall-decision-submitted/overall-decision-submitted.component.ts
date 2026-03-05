import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { AlrActionService } from '@actions/alr/core/alr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';

import { ALRApplicationSubmittedRequestActionPayload } from 'pmrv-api';

interface ViewModel {
  overallDecision: OverallVerificationAssessment;
}

@Component({
  selector: 'app-alr-overall-decision-submitted',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-action-task header="ALR verification overall decision" [breadcrumb]="true">
        <app-shared-overall-decision-summary-template
          [overallDecision]="vm.overallDecision"
          [isEditable]="false"></app-shared-overall-decision-summary-template>
      </app-action-task>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrOverallDecisionSubmittedComponent {
  payload = this.alrActionService.payload as Signal<ALRApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const overallDecision = this.payload().verificationReport?.overallAssessment as OverallVerificationAssessment;

    return {
      overallDecision,
    };
  });

  constructor(private readonly alrActionService: AlrActionService) {}
}
