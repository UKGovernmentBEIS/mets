import { TotalEmissionsFormProvider } from '@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions-form.provider';
import { TotalEmissionsSummaryComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions-summary/total-emissions-summary.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

describe('TotalEmissionsSummaryComponent', () => {
  let result: RenderResult<TotalEmissionsSummaryComponent>;

  beforeEach(async () => {
    result = await render(TotalEmissionsSummaryComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: TotalEmissionsFormProvider },
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
