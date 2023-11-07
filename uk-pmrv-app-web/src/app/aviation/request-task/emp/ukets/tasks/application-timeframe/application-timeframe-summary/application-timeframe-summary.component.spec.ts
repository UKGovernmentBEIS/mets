import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { ApplicationTimeframeFormProvider } from '../application-timeframe-form.provider';
import { ApplicationTimeframeSummaryComponent } from './application-timeframe-summary.component';

describe('ApplicationTimeframeSummaryComponent', () => {
  let result: RenderResult<ApplicationTimeframeSummaryComponent>;

  beforeEach(async () => {
    result = await render(ApplicationTimeframeSummaryComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ApplicationTimeframeFormProvider },
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
