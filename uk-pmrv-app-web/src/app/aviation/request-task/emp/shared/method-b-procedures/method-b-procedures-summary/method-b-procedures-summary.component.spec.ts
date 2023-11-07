import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { MethodBProceduresFormProvider } from '../method-b-procedures-form.provider';
import { MethodBProceduresSummaryComponent } from './method-b-procedures-summary.component';

describe('MethodBProceduresSummaryComponent', () => {
  let result: RenderResult<MethodBProceduresSummaryComponent>;

  beforeEach(async () => {
    result = await render(MethodBProceduresSummaryComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MethodBProceduresFormProvider },
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
