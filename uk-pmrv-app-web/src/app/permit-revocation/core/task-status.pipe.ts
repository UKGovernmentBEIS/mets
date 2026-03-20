import { Pipe, PipeTransform } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { TaskItemStatus } from '../../shared/task-list/task-list.interface';
import { PermitRevocationStore } from '../store/permit-revocation-store';
import { resolveApplyStatus, resolveWithDrawStatus } from './section-status';

@Pipe({
  name: 'taskStatus',
  standalone: false,
})
export class TaskStatusPipe implements PipeTransform {
  constructor(private readonly store: PermitRevocationStore) {}

  transform(key: string): Observable<TaskItemStatus> {
    return combineLatest([this.store, this.store.isFinalAlrVisible$]).pipe(
      map(([state, isFinalAlrVisible]) => {
        switch (key) {
          case 'REVOCATION_APPLY':
            return resolveApplyStatus(state, isFinalAlrVisible);
          case 'REVOCATION_WITHDRAW':
            return resolveWithDrawStatus(state);
        }
      }),
    );
  }
}
