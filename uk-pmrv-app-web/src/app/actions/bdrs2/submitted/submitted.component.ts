import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { BDRS2BaselineSummaryTemplateComponent } from '@shared/components/bdrs2/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { BDRS2, BDRS2ApplicationSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

import { Bdrs2ActionService } from '../core/bdrs2.service';
import { getBdrs2ActionTitle } from './submitted';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  bdrs2: BDRS2;
}

@Component({
  selector: 'app-bdrs2-action-submitted',
  standalone: true,
  imports: [ActionSharedModule, BDRS2BaselineSummaryTemplateComponent, NgIf, SharedModule],
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
})
export class Bdrs2SubmittedComponent {
  payload = this.bdrs2ActionService.payload as Signal<BDRS2ApplicationSubmittedRequestActionPayload>;

  requestActionType = this.bdrs2ActionService.requestActionType;

  vm: Signal<ViewModel> = computed(() => {
    const header = getBdrs2ActionTitle(this.requestActionType());
    const bdrs2 = this.payload().bdrs2;

    return {
      header,
      expectedActionType: [this.requestActionType()],
      bdrs2,
    };
  });

  constructor(private readonly bdrs2ActionService: Bdrs2ActionService) {}
}
