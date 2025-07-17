import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { BDRRegulatorReviewReturnedForAmendsRequestActionPayload } from 'pmrv-api';

import { BdrActionService } from '../core/bdr.service';

@Component({
  selector: 'app-bdr-returned-for-amends',
  templateUrl: './returned-for-amends.component.html',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnedForAmendsComponent {
  requestAction = this.bdrService.requestAction;
  payload = this.bdrService.payload;

  bdrTitle: Signal<string> = computed(() => {
    return this.requestAction()?.requestId?.split('-')[1] + ' baseline data report returned for amends';
  });

  decisionAmends = computed(() => {
    const amendsPayload = this.payload() as BDRRegulatorReviewReturnedForAmendsRequestActionPayload;
    return Object.keys(amendsPayload?.regulatorReviewGroupDecisions ?? [])
      .filter((key) => amendsPayload?.regulatorReviewGroupDecisions[key]?.['type'] === 'OPERATOR_AMENDS_NEEDED')
      .map((key) => ({ groupKey: key, data: amendsPayload.regulatorReviewGroupDecisions[key] }) as any);
  });

  constructor(readonly bdrService: BdrActionService) {}
}
