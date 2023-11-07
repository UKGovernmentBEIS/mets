import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaUncorrectedNonConformities, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: AviationAerCorsiaUncorrectedNonConformities;
}

@Component({
  selector: 'app-uncorrected-non-conformities',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, RouterLink, UncorrectedItemGroupComponent],
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <h2 class="govuk-heading-m">Non-conformities with the approved monitoring plan</h2>
      <dl govuk-summary-list [hasBorders]="true">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>
            Have there been any uncorrected non-conformities with the approved emissions monitoring plan?
          </dt>
          <dd govukSummaryListRowValue>
            {{ vm.data.existUncorrectedNonConformities ? 'Yes' : 'No' }}
          </dd>
        </div>
      </dl>
      <app-uncorrected-item-group
        *ngIf="vm.data.existUncorrectedNonConformities"
        [uncorrectedItems]="vm.data?.uncorrectedNonConformities"
      ></app-uncorrected-item-group>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedNonConformitiesComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectVerificationReport),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([verificationReport, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerVerifyHeaderTaskMap['uncorrectedNonConformities'],
      data: verificationReport.uncorrectedNonConformities,
    })),
  );
}
