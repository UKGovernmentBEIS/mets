import { Injectable } from '@angular/core';
import { CanActivate, CanDeactivate } from '@angular/router';

import { map, Observable, take, tap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '../store';

@Injectable()
export class ChangeAssigneeGuard implements CanActivate, CanDeactivate<unknown> {
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
