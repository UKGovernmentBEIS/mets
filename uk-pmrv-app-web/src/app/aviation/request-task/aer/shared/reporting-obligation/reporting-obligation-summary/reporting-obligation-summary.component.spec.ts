import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { ReportingObligationFormProvider } from '../reporting-obligation-form.provider';
import ReportingObligationSummaryComponent from './reporting-obligation-summary.component';

describe('ReportingObligationSummaryComponent', () => {
  let result: RenderResult<ReportingObligationSummaryComponent>;

  beforeEach(async () => {
    result = await render(ReportingObligationSummaryComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ReportingObligationFormProvider },
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
