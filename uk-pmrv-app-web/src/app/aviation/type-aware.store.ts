import { InjectionToken } from '@angular/core';

import { map, Observable, OperatorFunction, pipe } from 'rxjs';

import { RequestActionDTO, RequestTaskDTO, RequestTaskItemDTO } from 'pmrv-api';

interface TypeAwareState {
  requestTaskItem?: RequestTaskItemDTO;
  requestActionItem?: RequestActionDTO;
}

export const TYPE_AWARE_STORE = new InjectionToken<TypeAwareStore>('TypeAware store');

export interface TypeAwareStore {
  readonly type$: Observable<RequestTaskDTO['type'] | RequestActionDTO['type']>;
}

export const selectType: OperatorFunction<TypeAwareState, RequestTaskDTO['type'] | RequestActionDTO['type']> = pipe(
  map((state) => {
    return state.requestTaskItem ? state.requestTaskItem.requestTask.type : state.requestActionItem.type;
  }),
);
