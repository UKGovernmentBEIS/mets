import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { BdrActionService } from '@actions/bdr/core/bdr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';

import { BDRApplicationSubmittedRequestActionPayload } from 'pmrv-api';

interface ViewModel {
  overallDecision: OverallVerificationAssessment;
}

@Component({
  selector: 'app-overall-decision-submitted',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-action-task header="BDR verification overall decision" [breadcrumb]="true">
        <app-shared-overall-decision-summary-template
          [isEditable]="false"
          [overallDecision]="vm.overallDecision"></app-shared-overall-decision-summary-template>
      </app-action-task>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrOverallDecisionSubmittedComponent {
  payload = this.bdrActionService.payload as Signal<BDRApplicationSubmittedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const overallDecision = this.payload().verificationReport?.overallAssessment as OverallVerificationAssessment;

    return {
      overallDecision,
    };
  });

  constructor(private readonly bdrActionService: BdrActionService) {}
}
