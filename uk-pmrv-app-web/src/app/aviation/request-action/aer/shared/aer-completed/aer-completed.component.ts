import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { SharedModule } from '@shared/shared.module';

interface ViewModel {
  pageHeader: string;
  creationDate: string;
  reportingYear: number;
}

@Component({
  selector: 'app-aer-completed',
  imports: [SharedModule, RouterModule],
  template: `
    <app-request-action-heading
      *ngIf="vm$ | async as vm"
      [headerText]="vm.pageHeader"
      [timelineCreationDate]="vm.creationDate">
      <app-summary-header class="govuk-heading-m">Details</app-summary-header>
      <dl govuk-summary-list appGroupedSummaryList class="govuk-!-margin-bottom-6">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Emissions report</dt>
          <dd govukSummaryListRowValue>
            <a govukLink [routerLink]="['../..']">{{ vm.reportingYear }} emissions report</a>
          </dd>
        </div>
      </dl>
    </app-request-action-heading>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerCompletedComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
  ]).pipe(
    map(([payload, creationDate]) => {
      return {
        pageHeader: payload.reportingYear + ' emissions report reviewed',
        creationDate: creationDate,
        reportingYear: payload.reportingYear,
      };
    }),
  );

  constructor(private store: RequestActionStore) {}
}
