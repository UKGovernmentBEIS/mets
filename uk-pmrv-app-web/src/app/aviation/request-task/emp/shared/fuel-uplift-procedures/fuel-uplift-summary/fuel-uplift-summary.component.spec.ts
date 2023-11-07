import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { FuelUpliftProceduresFormProvider } from '../fuel-uplift-procedures-form.provider';
import { FuelUpliftSummaryComponent } from './fuel-uplift-summary.component';

describe('FuelUpliftSummaryComponent', () => {
  let result: RenderResult<FuelUpliftSummaryComponent>;

  beforeEach(async () => {
    result = await render(FuelUpliftSummaryComponent, {
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
