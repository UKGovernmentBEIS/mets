import { Pipe, PipeTransform } from '@angular/core';

import { Observable } from 'rxjs';

import { Permit, ProcedureForm } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { Path } from '../types/permit-task.type';

@Pipe({ name: 'taskProcedureForm' })
export class TaskProcedureFormPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore<PermitApplicationState>) {}

  transform<P extends Path<Permit>>(taskKey: P, currentPermit = true): Observable<ProcedureForm> {
    return currentPermit
      ? this.store.findTask<ProcedureForm>(taskKey)
      : this.store.findOriginalTask<ProcedureForm>(taskKey);
  }
}
