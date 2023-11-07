import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { BlockProceduresFormProvider } from '../block-procedures-form.provider';
import { BlockProceduresMonitoringSummaryComponent } from './block-procedures-monitoring-summary.component';

describe('BlockProceduresMonitoringSummaryComponent', () => {
  let result: RenderResult<BlockProceduresMonitoringSummaryComponent>;

  beforeEach(async () => {
    result = await render(BlockProceduresMonitoringSummaryComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: BlockProceduresFormProvider },
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
