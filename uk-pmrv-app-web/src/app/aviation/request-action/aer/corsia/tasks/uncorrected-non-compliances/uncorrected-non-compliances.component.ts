import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUncorrectedNonCompliances, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: AviationAerUncorrectedNonCompliances;
}

@Component({
  selector: 'app-uncorrected-non-compliances',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, RouterLink, UncorrectedItemGroupComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <h2 class="govuk-heading-m">Non-compliances with the Air Navigation Order</h2>
      <dl govuk-summary-list [hasBorders]="true">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Have there been any uncorrected non-compliances with the Air Navigation Order?</dt>
          <dd govukSummaryListRowValue>{{ vm.data.exist ? 'Yes' : 'No' }}</dd>
        </div>
      </dl>
      <app-uncorrected-item-group
        *ngIf="vm.data.exist"
        [uncorrectedItems]="vm.data.uncorrectedNonCompliances"
      ></app-uncorrected-item-group>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedNonCompliancesComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectVerificationReport),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([verificationReport, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerVerifyHeaderTaskMap['uncorrectedNonCompliances'],
      data: verificationReport.uncorrectedNonCompliances,
    })),
  );
}
