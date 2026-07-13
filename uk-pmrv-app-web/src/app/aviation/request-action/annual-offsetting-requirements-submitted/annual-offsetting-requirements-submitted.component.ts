import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { AnnualOffsettingRequirementsSummaryTemplateComponent } from '@aviation/shared/components/annual-offsetting-requirements-summary-template/annual-offsetting-requirements-summary-template.component';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsiaAnnualOffsetting,
  AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload,
} from 'pmrv-api';

import { RequestActionStore } from '../store';
import { getRequestActionHeader } from '../util';

interface ViewModel {
  pageHeader: string;
  creationDate: string;
  annualOffsetting: AviationAerCorsiaAnnualOffsetting;
}

@Component({
  selector: 'app-annual-offsetting-requirements-submitted',
  imports: [AnnualOffsettingRequirementsSummaryTemplateComponent, SharedModule, ActionSharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-request-action-heading [headerText]="vm.pageHeader" [timelineCreationDate]="vm.creationDate">
        <app-annual-offsetting-requirements-summary-template
          [annualOffsetting]="vm.annualOffsetting"
          [isEditable]="false"></app-annual-offsetting-requirements-summary-template>
        <app-action-recipients-template
          header="Recipients"
          officialNoticeText="Official notice"
          [isAviation]="true"></app-action-recipients-template>
      </app-request-action-heading>
    </ng-container>
  `,
  providers: [UserInfoResolverPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnnualOffsettingRequirementsSubmittedComponent {
  private readonly requestActionItem = toSignal(this.store.pipe(map((state) => state.requestActionItem)));

  vm: Signal<ViewModel> = computed(() => {
    const requestActionItem = this.requestActionItem();
    const payload =
      requestActionItem.payload as AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload;

    return {
      pageHeader: getRequestActionHeader(requestActionItem.type),
      creationDate: requestActionItem.creationDate,
      annualOffsetting: payload.aviationAerCorsiaAnnualOffsetting,
    } as ViewModel;
  });

  constructor(public store: RequestActionStore) {}
}
