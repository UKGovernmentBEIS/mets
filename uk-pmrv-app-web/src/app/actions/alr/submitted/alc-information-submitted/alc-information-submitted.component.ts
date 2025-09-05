import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { AlrActionService } from '@actions/alr/core/alr.service';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { AlrAlcInformationSummaryTemplateComponent } from '@shared/components/alr/alc-information-summary-template/alc-information-summary-template.component';

import { ALRApplicationProceededToAuthorityRequestActionPayload, ALRApplicationRegulatorReviewOutcome } from 'pmrv-api';

interface ViewModel {
  alc: ALRApplicationRegulatorReviewOutcome;
}

@Component({
  selector: 'app-alr-alc-information-submitted',
  standalone: true,
  imports: [ActionSharedModule, AlrAlcInformationSummaryTemplateComponent, NgIf],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-action-task header="Information about this activity level change" [breadcrumb]="true">
        <app-alr-alc-information-summary-template
          [data]="vm.alc"
          [editable]="false"></app-alr-alc-information-summary-template>
      </app-action-task>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAlcInformationSubmittedComponent {
  payload = this.alrActionService.payload as Signal<ALRApplicationProceededToAuthorityRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => ({ alc: this.payload().regulatorReviewOutcome }));

  constructor(private readonly alrActionService: AlrActionService) {}
}
