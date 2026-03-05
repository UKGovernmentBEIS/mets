import { Pipe, PipeTransform } from '@angular/core';

import { Observable } from 'rxjs';

import { Permit, ProcedureOptionalForm } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { Path } from '../types/permit-task.type';

@Pipe({ name: 'taskProcedureOptionalForm' })
export class TaskProcedureOptionalFormPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore<PermitApplicationState>) {}

  transform<P extends Path<Permit>>(taskKey: P, currentPermit = true): Observable<ProcedureOptionalForm> {
    return currentPermit
      ? this.store.findTask<ProcedureOptionalForm>(taskKey)
      : this.store.findOriginalTask<ProcedureOptionalForm>(taskKey);
  }
}
