import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { SharedModule } from '@shared/shared.module';

import { BatchReissueCompletedRequestActionPayload } from 'pmrv-api';

import { FiltersModel } from '../../../../shared/components/emp-batch-reissue/filters.model';
import { EmpBatchReissueFiltersTemplateComponent } from '../../../../shared/components/emp-batch-reissue/filters-template/filters-template.component';
import { query } from '../../emp-batch-reissue.selectors';

interface ViewModel {
  pageHeader: string;
  requestActionId: number;
  timelineCreationDate: string;
  payload: BatchReissueCompletedRequestActionPayload;
  filters: FiltersModel;
}

@Component({
  selector: 'app-emp-batch-reissue-completed',
  standalone: true,
  imports: [SharedModule, EmpBatchReissueFiltersTemplateComponent, RouterLink],
  templateUrl: './completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompletedComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestActionQuery.selectRequestActionItem),
    this.store.pipe(query.selectBatchReissueCompletedRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
  ]).pipe(
    map(([requestActionItem, payload, timelineCreationDate]) => ({
      pageHeader: 'Batch variation completed',
      requestActionId: requestActionItem.id,
      timelineCreationDate,
      payload,
      filters: {
        ...payload.filters,
        numberOfEmitters: payload.numberOfAccounts,
      } as FiltersModel,
    })),
  );
}
