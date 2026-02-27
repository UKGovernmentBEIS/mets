import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { getActionTitle } from '@actions/request-action.util';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { ChangesRequestedTemplateComponent } from '@shared/components/changes-requested-template/changes-requested-template.component';

import {
  RequestActionDTO,
  WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails,
  WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload,
} from 'pmrv-api';

import { WasteQdrActionService } from '../core/waste-qdr.service';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  decisionDetails: WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails;
  regulatorReviewAttachments: { [key: string]: string };
  downloadUrl: string;
}

@Component({
  selector: 'app-waste-qdr-action-returned-for-amends',
  imports: [ActionSharedModule, NgIf, ChangesRequestedTemplateComponent],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-base-action-container-component
        [header]="vm.header"
        [customContentTemplate]="customContentTemplate"
        [expectedActionType]="vm.expectedActionType"></app-base-action-container-component>

      <ng-template #customContentTemplate>
        <h2 app-summary-header class="govuk-heading-m">Quarterly data report</h2>

        <app-changes-requested-template
          [requiredChanges]="$any(vm.decisionDetails)?.requiredChanges"
          [reviewAttachments]="vm.regulatorReviewAttachments"
          [downloadUrl]="vm.downloadUrl"
          [notes]="vm.decisionDetails?.notes"></app-changes-requested-template>
      </ng-template>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrActionReturnedForAmendsComponent {
  private readonly requestActionType = this.wasteQdrActionService.requestActionType;
  private readonly payload = this.wasteQdrActionService
    .payload as Signal<WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const header = getActionTitle(this.requestActionType());
    const {
      regulatorReviewAttachments,
      reviewDecision: { details },
    } = this.payload();

    return {
      header,
      expectedActionType: [this.requestActionType()],
      regulatorReviewAttachments,
      decisionDetails: details as WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails,
      downloadUrl: this.wasteQdrActionService.getBaseFileDownloadUrl(),
    };
  });

  constructor(private readonly wasteQdrActionService: WasteQdrActionService) {}
}
