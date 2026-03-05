import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { BDRVerificationReturnedToOperatorRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { BdrActionService } from '../core/bdr.service';
import { getBdrActionTitle } from '../submitted/submitted';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  changesRequired: BDRVerificationReturnedToOperatorRequestActionPayload['changesRequired'];
}

@Component({
  selector: 'app-bdr-action-returned-to-operator',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SharedModule],
  templateUrl: './returned-to-operator.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrReturnedToOperatorComponent {
  payload = this.bdrActionService.payload as Signal<BDRVerificationReturnedToOperatorRequestActionPayload>;
  requestActionType = this.bdrActionService.requestActionType;

  vm: Signal<ViewModel> = computed(() => {
    const header = getBdrActionTitle(this.requestActionType());
    const changesRequired = this.payload().changesRequired;

    return {
      header,
      expectedActionType: [this.requestActionType()],
      changesRequired,
    };
  });

  constructor(private readonly bdrActionService: BdrActionService) {}
}
