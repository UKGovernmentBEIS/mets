import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { MonitoringApproachFormProvider } from '../monitoring-approach-form.provider';
import { MonitoringSummaryComponent } from './monitoring-summary.component';

describe('MonitoringSummaryComponent', () => {
  let result: RenderResult<MonitoringSummaryComponent>;

  beforeEach(async () => {
    result = await render(MonitoringSummaryComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MonitoringApproachFormProvider },
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
