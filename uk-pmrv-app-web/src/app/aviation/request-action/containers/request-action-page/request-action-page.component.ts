import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { TaskSection } from '@shared/task-list/task-list.interface';

import { RequestActionDTO, RequestActionPayload } from 'pmrv-api';

import { requestActionQuery, RequestActionStore } from '../../store';
import { getApplicationSubmittedTasks, getRequestActionHeader } from '../../util';

interface ViewModel {
  header: string;
  creationDate: string;
  sections: TaskSection<any>[];
  requestType: RequestActionDTO['requestType'];
  payload: RequestActionPayload;
}

@Component({
  selector: 'app-request-action-page',
  templateUrl: './request-action-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
  styles: [
    `
      :host ::ng-deep .app-task-list {
        list-style-type: none;
        padding-left: 0;
      }
    `,
  ],
})
export class RequestActionPageComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestActionQuery.selectRequestActionItem),
    this.store.pipe(requestActionQuery.selectCreationDate),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectIsCorsia),
  ]).pipe(
    map(([requestAction, creationDate, regulatorViewer, isCorsia]) => {
      return {
        header: getRequestActionHeader(requestAction.type, requestAction.payload),
        creationDate: creationDate,
        sections: getApplicationSubmittedTasks(requestAction.type, requestAction.payload, regulatorViewer, isCorsia),
        requestType: requestAction.requestType,
        payload: requestAction.payload,
      };
    }),
  );

  constructor(private readonly store: RequestActionStore) {}
}
