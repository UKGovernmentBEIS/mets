import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { ThreeYearOffsettingRequirementsTableTemplateComponent } from '@aviation/shared/components/offsetting-requirements-table-template/offsetting-requirements-table-template.component';
import { AviationAerCorsia3YearPeriodOffsettingTableData } from '@aviation/shared/types';
import { getTableData } from '@aviation/shared/utils/3year-period-offsetting-shared.util';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsia3YearPeriodOffsetting,
  AviationAerCorsia3YearPeriodOffsettingApplicationSubmittedRequestActionPayload,
} from 'pmrv-api';

import { RequestActionStore } from '../store';
import { getRequestActionHeader } from '../util';

interface ViewModel {
  pageHeader: string;
  creationDate: string;
  tableData: AviationAerCorsia3YearPeriodOffsettingTableData[];
  operatorHaveOffsettingRequirements: AviationAerCorsia3YearPeriodOffsetting['operatorHaveOffsettingRequirements'];
}

@Component({
  selector: 'app-three-year-period-offsetting-requirements-submitted',
  imports: [SharedModule, ThreeYearOffsettingRequirementsTableTemplateComponent, ActionSharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-request-action-heading [headerText]="vm.pageHeader" [timelineCreationDate]="vm.creationDate">
        <app-3year-offsetting-requirements-table-template
          [useTableWithInputs]="false"
          [data]="vm.tableData"></app-3year-offsetting-requirements-table-template>

        <dl govuk-summary-list [hasBorders]="true">
          <div govukSummaryListRow>
            <dt govukSummaryListRowKey>Does the operator have any offsetting requirements for this period?</dt>
            <dd govukSummaryListRowValue>
              {{ vm.operatorHaveOffsettingRequirements ? 'Yes' : 'No' }}
            </dd>
          </div>
        </dl>
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
export class ThreeYearPeriodOffsettingRequirementsSubmittedComponent {
  private readonly requestActionItem = toSignal(this.store.pipe(map((state) => state.requestActionItem)));

  vm: Signal<ViewModel> = computed(() => {
    const requestActionItem = this.requestActionItem();
    const aviationAerCorsia3YearPeriodOffsetting = (
      requestActionItem.payload as AviationAerCorsia3YearPeriodOffsettingApplicationSubmittedRequestActionPayload
    ).aviationAerCorsia3YearPeriodOffsetting;
    const tableData = getTableData(aviationAerCorsia3YearPeriodOffsetting);

    return {
      pageHeader: getRequestActionHeader(requestActionItem.type),
      creationDate: requestActionItem.creationDate,
      tableData,
      operatorHaveOffsettingRequirements: aviationAerCorsia3YearPeriodOffsetting.operatorHaveOffsettingRequirements,
    } as ViewModel;
  });

  constructor(public store: RequestActionStore) {}
}
