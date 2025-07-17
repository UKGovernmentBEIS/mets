import { Injectable } from '@angular/core';

import { map, Observable, take, tap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '../store';

@Injectable()
export class ChangeAssigneeGuard {
  private reassignedTo: string;

  constructor(private readonly store: RequestTaskStore) {}

  canActivate(): Observable<boolean> {
    return this.store.pipe(
      requestTaskQuery.selectTaskReassignedTo,
      take(1),
      tap((reassignedTo) => {
        this.reassignedTo = reassignedTo;
        this.store.setIsTaskReassigned(false);
        this.store.setTaskReassignedTo(null);
      }),
      map(() => true),
    );
  }

  canDeactivate(): Observable<boolean> {
    return this.store.pipe(requestTaskQuery.selectTaskReassignedTo).pipe(
      take(1),
      tap((reassignedTo) => {
        if (!reassignedTo && !!this.reassignedTo) {
          this.store.setTaskReassignedTo(this.reassignedTo);
          this.store.setIsTaskReassigned(true);
        }
      }),
      map(() => true),
    );
  }
}
