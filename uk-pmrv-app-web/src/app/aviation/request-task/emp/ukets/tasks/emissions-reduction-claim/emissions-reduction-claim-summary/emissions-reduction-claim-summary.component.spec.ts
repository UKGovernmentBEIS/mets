import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { EmissionsReductionClaimSummaryComponent } from './emissions-reduction-claim-summary.component';

describe('EmissionsReductionClaimSummaryComponent', () => {
  let result: RenderResult<EmissionsReductionClaimSummaryComponent>;

  beforeEach(async () => {
    result = await render(EmissionsReductionClaimSummaryComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider },
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
