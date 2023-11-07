import { Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class ReturnOfAllowancesService {
  constructor(private readonly store: CommonActionsStore) {}

  getPayload(): Observable<any> {
    return this.store.payload$.pipe(map((payload) => payload));
  }
}
