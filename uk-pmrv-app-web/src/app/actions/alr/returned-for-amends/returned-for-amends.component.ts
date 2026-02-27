import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { ALRRegulatorReviewReturnedForAmendsRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { AlrActionService } from '../core/alr.service';
import { getAlrActionTitle } from '../utils';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  decisionAmends: Array<any>;
}

@Component({
  selector: 'app-alr-returned-for-amends',
  imports: [ActionSharedModule, SharedModule],
  templateUrl: './returned-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrReturnedForAmendsComponent {
  private readonly requestActionType = this.alrActionService.requestActionType;
  private readonly payload = this.alrActionService
    .payload as Signal<ALRRegulatorReviewReturnedForAmendsRequestActionPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const header = getAlrActionTitle(this.requestActionType());
    const payload = this.payload();

    return {
      header,
      expectedActionType: [this.requestActionType()],
      decisionAmends: Object.keys(payload?.regulatorReviewGroupDecisions ?? [])
        .filter((key) => payload?.regulatorReviewGroupDecisions[key]?.['type'] === 'OPERATOR_AMENDS_NEEDED')
        .map((key) => ({ groupKey: key, data: payload.regulatorReviewGroupDecisions[key] }) as any),
    };
  });

  constructor(readonly alrActionService: AlrActionService) {}
}
