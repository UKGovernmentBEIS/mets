import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { RequestActionDTO } from 'pmrv-api';

import { NerActionService } from '../core/ner.service';
import { getNerActionTitle } from '../utils';

export interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
}

@Component({
  selector: 'app-ner-action-submitted',
  imports: [ActionSharedModule, SharedModule],
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerSubmittedComponent {
  private readonly nerActionService = inject(NerActionService);
  private readonly requestActionType = this.nerActionService.requestActionType;

  vm: Signal<ViewModel> = computed(() => {
    const header = getNerActionTitle(this.requestActionType());

    return {
      header,
      expectedActionType: [this.requestActionType()],
    };
  });
}
