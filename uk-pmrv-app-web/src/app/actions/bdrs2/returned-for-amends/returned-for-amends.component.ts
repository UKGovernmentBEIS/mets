import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload } from 'pmrv-api';

import { Bdrs2ActionService } from '../core/bdrs2.service';

@Component({
  selector: 'app-bdrs2-returned-for-amends',
  imports: [ActionSharedModule, SharedModule],
  templateUrl: './returned-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2ReturnedForAmendsComponent {
  requestAction = this.bdrs2Service.requestAction;
  payload = this.bdrs2Service.payload;

  bdrTitle: Signal<string> = computed(() => {
    return this.requestAction()?.requestId?.split('-')[2] + ' stage 2 baseline data report returned for amendments';
  });

  decisionAmends = computed(() => {
    const amendsPayload = this.payload() as BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload;
    return Object.keys(amendsPayload?.regulatorReviewGroupDecisions ?? [])
      .filter((key) => amendsPayload?.regulatorReviewGroupDecisions[key]?.['type'] === 'OPERATOR_AMENDS_NEEDED')
      .map((key) => ({ groupKey: key, data: amendsPayload.regulatorReviewGroupDecisions[key] }) as any);
  });

  constructor(readonly bdrs2Service: Bdrs2ActionService) {}
}
