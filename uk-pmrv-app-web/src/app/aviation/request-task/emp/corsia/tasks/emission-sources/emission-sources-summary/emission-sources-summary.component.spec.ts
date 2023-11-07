import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { AircraftTypeFormProvider } from '../aircraft-type/aircraft-type-form.provider';
import { EmissionSourcesCorsiaFormProvider } from '../emission-sources-form.provider';
import { EmissionSourcesSummaryComponent } from './emission-sources-summary.component';

describe('EmissionSourcesPageComponent', () => {
  let result: RenderResult<EmissionSourcesSummaryComponent>;

  beforeEach(async () => {
    result = await render(EmissionSourcesSummaryComponent, {
      providers: [
        EmissionSourcesCorsiaFormProvider,
        AircraftTypeFormProvider,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });
  });

  it('smoke test', () => {
    const {
      fixture: { componentInstance: component },
    } = result;

    expect(component).toBeTruthy();
  });
});
