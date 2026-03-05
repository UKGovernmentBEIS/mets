import { Pipe, PipeTransform } from '@angular/core';

import { Observable } from 'rxjs';

import { Permit } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Pipe({ name: 'task' })
export class TaskPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore<PermitApplicationState>) {}

  transform<K extends keyof Permit>(key: K, currentPermit = true): Observable<Permit[K]> {
    return currentPermit ? this.store.getTask(key) : this.store.getOriginalTask(key);
  }
}
