import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { MethodAProceduresFormProvider } from '../method-a-procedures-form.provider';
import { MethodAProceduresSummaryComponent } from './method-a-procedures-summary.component';

describe('MethodAProceduresSummaryComponent', () => {
  let result: RenderResult<MethodAProceduresSummaryComponent>;

  beforeEach(async () => {
    result = await render(MethodAProceduresSummaryComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MethodAProceduresFormProvider },
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
