import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { AbbreviationsFormProvider } from '../abbreviations-form.provider';
import { AbbreviationsSummaryComponent } from './abbreviations-summary.component';

describe('AbbreviationsSummaryComponent', () => {
  let result: RenderResult<AbbreviationsSummaryComponent>;

  beforeEach(async () => {
    result = await render(AbbreviationsSummaryComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AbbreviationsFormProvider },
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
