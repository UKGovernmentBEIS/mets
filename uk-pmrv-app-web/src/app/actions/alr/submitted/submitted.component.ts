import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { ALRApplicationSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { AlrActionService } from '../core/alr.service';
import { getAlrActionTitle } from '../utils';

export interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  hasVerificationReport: boolean;
}

@Component({
  selector: 'app-alr-action-submitted',
  standalone: true,
  imports: [ActionSharedModule, NgIf, SharedModule],
  templateUrl: './submitted.component.html',
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrSubmittedComponent {
  private readonly payload = this.alrActionService.payload;
  private readonly requestActionType = this.alrActionService.requestActionType;

  private readonly hasVerificationReport = computed(() => {
    return !!(this.payload() as ALRApplicationSubmittedRequestActionPayload).verificationReport;
  });

  vm: Signal<ViewModel> = computed(() => {
    const header = getAlrActionTitle(this.requestActionType());

    return {
      header,
      expectedActionType: [this.requestActionType()],
      hasVerificationReport: this.hasVerificationReport(),
    };
  });

  constructor(private readonly alrActionService: AlrActionService) {}
}
