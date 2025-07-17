import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { ALRVerificationReturnedToOperatorRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { AlrActionService } from '../core/alr.service';
import { getAlrActionTitle } from '../utils';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  changesRequired: ALRVerificationReturnedToOperatorRequestActionPayload['changesRequired'];
}

@Component({
  selector: 'app-alr-action-returned-to-operator',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SharedModule],
  templateUrl: './returned-to-operator.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrReturnedToOperatorComponent {
  payload = this.alrActionService.payload as Signal<ALRVerificationReturnedToOperatorRequestActionPayload>;
  requestActionType = this.alrActionService.requestActionType;

  vm: Signal<ViewModel> = computed(() => {
    const header = getAlrActionTitle(this.requestActionType());
    const changesRequired = this.payload().changesRequired;

    return {
      header,
      expectedActionType: [this.requestActionType()],
      changesRequired,
    };
  });

  constructor(private readonly alrActionService: AlrActionService) {}
}
