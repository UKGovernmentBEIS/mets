import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { ReissueCompletedRequestActionPayload } from 'pmrv-api';

import { SharedModule } from '../../../../../shared/shared.module';
import { RequestActionTaskComponent } from '../../../shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '../../../store';
import { query } from '../../emp-batch-reissue.selectors';

interface ViewModel {
  pageHeader: string;
  requestActionId: number;
  timelineCreationDate: string;
  payload: ReissueCompletedRequestActionPayload;
}

@Component({
  selector: 'app-emp-reissue-completed',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, RouterLink],
  templateUrl: './completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompletedComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestActionQuery.selectRequestActionItem),
    this.store.pipe(query.selectReissueCompletedRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
  ]).pipe(
    map(([requestActionItem, payload, timelineCreationDate]) => ({
      pageHeader: 'Batch variation completed',
      requestActionId: requestActionItem.id,
      timelineCreationDate,
      payload,
    })),
  );
}
