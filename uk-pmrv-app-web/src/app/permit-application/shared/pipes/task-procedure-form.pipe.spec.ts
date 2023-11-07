import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { ProcedureForm, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { TaskProcedureFormPipe } from './task-procedure-form.pipe';

describe('TaskProcedureFormPipe', () => {
  let pipe: TaskProcedureFormPipe;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [TaskProcedureFormPipe],
      providers: [
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });

    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => (pipe = new TaskProcedureFormPipe(store)));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return an observable of the task procedure form', async () => {
    const assessAndControlRisk: ProcedureForm = {
      locationOfRecords: 'fff',
      procedureDescription: 'fff',
      procedureDocumentName: 'fff',
      procedureReference: 'fff',
      responsibleDepartmentOrRole: 'fff',
    };

    store.setState({ ...store.getState(), permit: { ...store.permit, assessAndControlRisk } });

    await expect(firstValueFrom(pipe.transform('assessAndControlRisk'))).resolves.toEqual(assessAndControlRisk);
  });
});
