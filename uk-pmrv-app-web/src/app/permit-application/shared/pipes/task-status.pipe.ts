import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { StatusKey } from '../types/permit-task.type';
import { resolvePermitSectionStatus } from '../utils/permit-section-status-resolver';

@Pipe({ name: 'taskStatus' })
export class TaskStatusPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore<PermitApplicationState>) {}

  transform(key: StatusKey, index?: number): Observable<TaskItemStatus> {
    return this.store.pipe(map((state) => resolvePermitSectionStatus(state, key, index)));
  }
}
