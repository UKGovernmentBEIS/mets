import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { EmissionSourcesFormProvider } from '../../emission-sources-form.provider';
import { AircraftTypeFormProvider } from '../aircraft-type-form.provider';
import { AircraftSearchPageComponent } from './aircraft-search-page.component';

describe('AircraftSearchPage', () => {
  let result: RenderResult<AircraftSearchPageComponent>;

  beforeEach(async () => {
    result = await render(AircraftSearchPageComponent, {
      providers: [
        EmissionSourcesFormProvider,
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
