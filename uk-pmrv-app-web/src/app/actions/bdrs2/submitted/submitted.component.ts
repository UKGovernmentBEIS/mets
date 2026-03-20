import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import {
  BDRS2,
  BDRS2ApplicationCompletedRequestActionPayload,
  BDRS2ApplicationSubmittedRequestActionPayload,
  RequestActionDTO,
} from 'pmrv-api';

import { Bdrs2ActionService } from '../core/bdrs2.service';
import { getBdrs2ActionTitle } from './submitted';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  bdrs2: BDRS2;
  hasVerificationReport: boolean;
  hasOutcome: boolean;
}

@Component({
  selector: 'app-bdrs2-action-submitted',
  imports: [ActionSharedModule, SharedModule],
  standalone: true,
  templateUrl: './submitted.component.html',
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2SubmittedComponent {
  payload = this.bdrs2ActionService.payload as Signal<BDRS2ApplicationSubmittedRequestActionPayload>;
  completedPayload = this.bdrs2ActionService.payload as Signal<BDRS2ApplicationCompletedRequestActionPayload>;

  requestActionType = this.bdrs2ActionService.requestActionType;

  readonly hasVerificationReport = computed(() => {
    const payload = this.payload();
    return !!payload.verificationReport;
  });

  readonly hasOutcome = computed(() => {
    return !!this.completedPayload().regulatorReviewOutcome;
  });

  vm: Signal<ViewModel> = computed(() => {
    const header = getBdrs2ActionTitle(this.requestActionType());
    const bdrs2 = this.payload().bdrs2;

    return {
      header,
      expectedActionType: [this.requestActionType()],
      bdrs2,
      hasVerificationReport: this.hasVerificationReport(),
      hasOutcome: this.hasOutcome(),
    };
  });

  constructor(private readonly bdrs2ActionService: Bdrs2ActionService) {}
}
