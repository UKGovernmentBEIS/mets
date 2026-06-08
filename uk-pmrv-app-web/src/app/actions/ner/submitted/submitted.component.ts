import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import {
  NERApplicationCompletedRequestActionPayload,
  NERApplicationSubmittedRequestActionPayload,
  RequestActionDTO,
} from 'pmrv-api';

import { NerActionService } from '../core/ner.service';
import { getNerActionTitle } from '../utils';

export interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  hasVerificationReport: boolean;
  hasOutcome: boolean;
}

@Component({
  selector: 'app-ner-action-submitted',
  imports: [ActionSharedModule, SharedModule],
  templateUrl: './submitted.component.html',
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerSubmittedComponent {
  private readonly nerActionService = inject(NerActionService);
  private readonly requestActionType = this.nerActionService.requestActionType;
  private readonly payload = this.nerActionService.payload as Signal<
    NERApplicationSubmittedRequestActionPayload & NERApplicationCompletedRequestActionPayload
  >;

  vm: Signal<ViewModel> = computed(() => {
    const header = getNerActionTitle(this.requestActionType());
    const payload = this.payload();

    return {
      header,
      expectedActionType: [this.requestActionType()],
      hasVerificationReport: !!payload.verificationReport,
      hasOutcome: !!payload.regulatorReviewOutcome,
    };
  });
}
