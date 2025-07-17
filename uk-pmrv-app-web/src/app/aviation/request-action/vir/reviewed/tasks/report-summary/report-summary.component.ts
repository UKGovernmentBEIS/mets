import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { virQuery } from '@aviation/request-action/vir/vir.selectors';
import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import { RegulatorReviewResponse, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  header: string;
  regulatorReviewResponse: RegulatorReviewResponse;
}

@Component({
  selector: 'app-report-summary',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, VirSharedModule],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.header"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-regulator-create-summary
        [regulatorReviewResponse]="vm.regulatorReviewResponse"
        [isEditable]="false"
        [isReview]="true"></app-regulator-create-summary>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportSummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(virQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => ({
      requestActionType: requestActionType,
      header: 'Create summary',
      regulatorReviewResponse: payload.regulatorReviewResponse,
    })),
  );

  constructor(public store: RequestActionStore) {}
}
