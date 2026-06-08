import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { ChangesRequestedTemplateComponent } from '@shared/components/changes-requested-template/changes-requested-template.component';
import { SharedModule } from '@shared/shared.module';

import { NERRegulatorReviewReturnedForAmendsRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { NerActionService } from '../core';

interface ViewModel {
  expectedActionType: Array<RequestActionDTO['type']>;
  decisionDetails: any;
  regulatorReviewAttachments: { [key: string]: string };
  downloadUrl: string;
}

@Component({
  selector: 'app-ner-returned-for-amends',
  imports: [ActionSharedModule, SharedModule, ChangesRequestedTemplateComponent],
  templateUrl: './returned-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerReturnedForAmendsComponent {
  private readonly requestActionType = this.nerService.requestActionType;
  payload = this.nerService.payload;

  vm: Signal<ViewModel> = computed(() => {
    const { regulatorReviewGroupDecisions, regulatorReviewAttachments } =
      this.payload() as NERRegulatorReviewReturnedForAmendsRequestActionPayload;

    return {
      decisionDetails: regulatorReviewGroupDecisions?.['NER']?.['details'],
      expectedActionType: [this.requestActionType()],
      regulatorReviewAttachments,
      downloadUrl: this.nerService.getBaseFileDownloadUrl(),
    };
  });

  constructor(readonly nerService: NerActionService) {}
}
