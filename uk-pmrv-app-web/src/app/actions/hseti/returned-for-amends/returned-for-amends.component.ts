import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { HSETIRegulatorReviewReturnedForAmendsRequestActionPayload } from 'pmrv-api';

import { HseTiActionService } from '../core/hseti.service';

@Component({
  selector: 'app-hseti-returned-for-amends',
  templateUrl: './returned-for-amends.component.html',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HsetiReturnedForAmendsComponent {
  requestAction = this.hsetiService.requestAction;
  payload = this.hsetiService.payload;
  allocationPeriod = this.hsetiService?.allocationPeriod as Signal<string>;

  hsetiTitle: Signal<string> = computed(() => {
    return (
      this.requestAction()?.requestId?.split('-')[1]?.replace(/_/g, '-') +
      ' HSE target increase details returned for amends'
    );
  });

  decisionAmends = computed(() => {
    const amendsPayload = this.payload() as HSETIRegulatorReviewReturnedForAmendsRequestActionPayload;
    return Object.keys(amendsPayload?.regulatorReviewGroupDecisions ?? [])
      .filter((key) => amendsPayload?.regulatorReviewGroupDecisions?.[key]?.['type'] === 'OPERATOR_AMENDS_NEEDED')
      .map((key) => ({ groupKey: key, data: amendsPayload?.regulatorReviewGroupDecisions?.[key] }) as any);
  });

  constructor(readonly hsetiService: HseTiActionService) {}
}
