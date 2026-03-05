import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { HseTiActionService } from '@actions/hseti/core/hseti.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';
import { OverallDecisionSummaryTemplateComponent } from '@tasks/hseti/shared/components/overall-decision-summary-template/overall-decision-summary-template.component';

import { HSETICompletedRequestActionPayload, HSETIRegulatorReviewOverallDecision } from 'pmrv-api';

interface ViewModel {
  overallDecision: HSETIRegulatorReviewOverallDecision;
}

@Component({
  selector: 'app-hseti-overall-decision-submitted',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SharedModule, OverallDecisionSummaryTemplateComponent],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-action-task header="Overall decision" [breadcrumb]="true">
        <app-overall-decision-summary-template
          [overallDecision]="vm.overallDecision"
          [isEditable]="false"></app-overall-decision-summary-template>
      </app-action-task>
    </ng-container>
  `,

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HsetiOverallDecisionSubmittedComponent {
  payload = this.alrActionService.payload as Signal<HSETICompletedRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const overallDecision = this.payload()?.overallDecision as HSETIRegulatorReviewOverallDecision;

    return {
      overallDecision,
    };
  });

  constructor(private readonly alrActionService: HseTiActionService) {}
}
