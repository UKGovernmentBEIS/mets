import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { FuelUpliftProceduresFormProvider } from '../fuel-uplift-procedures-form.provider';
import { FuelUpliftRecordsComponent } from './fuel-uplift-records.component';

describe('FuelUpliftRecordsComponent', () => {
  let result: RenderResult<FuelUpliftRecordsComponent>;

  beforeEach(async () => {
    result = await render(FuelUpliftRecordsComponent, {
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
