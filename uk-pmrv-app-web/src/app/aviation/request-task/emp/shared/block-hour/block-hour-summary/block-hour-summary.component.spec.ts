import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { BlockHourProceduresFormProvider } from '../block-hour-procedures-form.provider';
import { BlockHourSummaryComponent } from './block-hour-summary.component';

describe('BlockHourSummaryComponent', () => {
  let result: RenderResult<BlockHourSummaryComponent>;

  beforeEach(async () => {
    result = await render(BlockHourSummaryComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: BlockHourProceduresFormProvider },
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
