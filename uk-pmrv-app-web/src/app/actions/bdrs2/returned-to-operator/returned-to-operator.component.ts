import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { BDRS2VerificationReturnedToOperatorRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { Bdrs2ActionService } from '../core/bdrs2.service';
import { getBdrs2ActionTitle } from '../submitted/submitted';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  changesRequired: BDRS2VerificationReturnedToOperatorRequestActionPayload['changesRequired'];
}

@Component({
  selector: 'app-bdrs2-action-returned-to-operator',
  imports: [ActionSharedModule, SharedModule],
  standalone: true,
  templateUrl: './returned-to-operator.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2ReturnedToOperatorComponent {
  payload = this.bdrs2ActionService.payload as Signal<BDRS2VerificationReturnedToOperatorRequestActionPayload>;
  requestActionType = this.bdrs2ActionService.requestActionType;

  vm: Signal<ViewModel> = computed(() => {
    const header = getBdrs2ActionTitle(this.requestActionType());
    const changesRequired = this.payload().changesRequired;

    return {
      header,
      expectedActionType: [this.requestActionType()],
      changesRequired,
    };
  });

  constructor(private readonly bdrs2ActionService: Bdrs2ActionService) {}
}
