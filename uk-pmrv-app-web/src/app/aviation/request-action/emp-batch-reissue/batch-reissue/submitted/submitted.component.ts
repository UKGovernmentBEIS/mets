import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { SharedModule } from '@shared/shared.module';

import { BatchReissueSubmittedRequestActionPayload } from 'pmrv-api';

import { FiltersModel } from '../../../../shared/components/emp-batch-reissue/filters.model';
import { EmpBatchReissueFiltersTemplateComponent } from '../../../../shared/components/emp-batch-reissue/filters-template/filters-template.component';
import { query } from '../../emp-batch-reissue.selectors';

interface ViewModel {
  pageHeader: string;
  timelineCreationDate: string;
  payload: BatchReissueSubmittedRequestActionPayload;
  filters: FiltersModel;
}

@Component({
  selector: 'app-emp-batch-reissue-submitted',
  standalone: true,
  imports: [SharedModule, EmpBatchReissueFiltersTemplateComponent, RouterLink],
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(query.selectBatchReissueSubmittedRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
  ]).pipe(
    map(([payload, timelineCreationDate]) => ({
      pageHeader: 'Batch variation submitted',
      timelineCreationDate,
      payload,
      filters: {
        ...payload.filters,
        numberOfEmitters: null,
      } as FiltersModel,
    })),
  );
}
