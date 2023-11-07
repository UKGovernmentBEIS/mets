import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { RecommendedImprovementsGroupComponent } from '@aviation/shared/components/aer-verify/recommended-improvements-group/recommended-improvements-group.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerRecommendedImprovements, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  recommendedImprovements: AviationAerRecommendedImprovements;
}

@Component({
  selector: 'app-recommended-improvements',
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <h2 class="govuk-heading-m">Recommended improvements</h2>
      <dl govuk-summary-list [hasBorders]="true">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Are there any recommended improvements?</dt>
          <dd govukSummaryListRowValue>{{ vm.recommendedImprovements.exist ? 'Yes' : 'No' }}</dd>
        </div>
      </dl>

      <app-recommended-improvements-group-template
        *ngIf="vm.recommendedImprovements.exist"
        [verifierComments]="vm.recommendedImprovements.recommendedImprovements"
      ></app-recommended-improvements-group-template>
    </app-request-action-task>
  `,
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, RecommendedImprovementsGroupComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class RecommendedImprovementsComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => {
      return {
        requestActionType,
        pageHeader: aerVerifyHeaderTaskMap['recommendedImprovements'],
        recommendedImprovements: {
          ...payload.verificationReport.recommendedImprovements,
        },
      };
    }),
  );
}
