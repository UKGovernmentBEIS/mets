import { ChangeDetectionStrategy, Component, OnInit, Type } from '@angular/core';
import { Title } from '@angular/platform-browser';

import { combineLatest, map, Observable } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';
import { TaskSection } from '@shared/task-list/task-list.interface';

import { RequestActionDTO, RequestActionPayload } from 'pmrv-api';

import { requestActionQuery, RequestActionStore } from '../../store';
import {
  getApplicationSubmittedTasks,
  getRequestActionHeader,
  peerReviewRequestActionTypes,
  reportableRequestActionTypes,
  reportComponentRequestActionTypes,
} from '../../util';

interface ViewModel {
  header: string;
  creationDate: string;
  sections: TaskSection<any>[];
  requestType: RequestActionDTO['requestType'];
  requestActionType: RequestActionDTO['type'];
  payload: RequestActionPayload;
  dataComponent: Type<any>;
  isPeerReview: boolean;
}

@Component({
  selector: 'app-request-action-page',
  templateUrl: './request-action-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
})
export class RequestActionPageComponent implements OnInit {
  hasReport$ = this.store.pipe(
    requestActionQuery.selectRequestActionType,
    map((requestActionType) => reportableRequestActionTypes.includes(requestActionType)),
  );

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
        requestActionType: requestAction.type,
        payload: requestAction.payload,
        isPeerReview: peerReviewRequestActionTypes.includes(requestAction.type),
        dataComponent: reportComponentRequestActionTypes.find((entry) =>
          entry.requestActionTypes.includes(requestAction.type),
        )?.reportComponent,
      };
    }),
  );

  constructor(
    private readonly store: RequestActionStore,
    private readonly titleService: Title,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        requestActionQuery.selectRequestActionType,
        map((actionType) => {
          const actionTypePipe = new ItemActionTypePipe();
          return actionTypePipe.transform(actionType);
        }),
      )
      .subscribe((title) => this.titleService.setTitle(title));
  }
}
