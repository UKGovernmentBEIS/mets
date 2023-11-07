import { AsyncValidatorFn, ValidationErrors } from '@angular/forms';

import { combineLatest, first, map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';

export function reviewGroupTasksCompletedValidator(statuses: Observable<TaskItemStatus>[]): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    combineLatest([...statuses]).pipe(
      first(),
      map(([...groupTaskStatuses]: TaskItemStatus[]) =>
        groupTaskStatuses.some((status) => status !== 'complete')
          ? { atLeastOne: 'All sections must be completed' }
          : null,
      ),
    );
}
