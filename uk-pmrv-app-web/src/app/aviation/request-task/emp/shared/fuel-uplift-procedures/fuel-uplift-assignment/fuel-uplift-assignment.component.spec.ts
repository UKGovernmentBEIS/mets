import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { FuelUpliftProceduresFormProvider } from '../fuel-uplift-procedures-form.provider';
import { FuelUpliftAssignmentComponent } from './fuel-uplift-assignment.component';

describe('FuelUpliftAssignmentComponent', () => {
  let result: RenderResult<FuelUpliftAssignmentComponent>;

  beforeEach(async () => {
    result = await render(FuelUpliftAssignmentComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: FuelUpliftProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });
  });

  it('should create', () => {
    const {
      fixture: { componentInstance: component },
    } = result;

    expect(component).toBeTruthy();
  });
});
