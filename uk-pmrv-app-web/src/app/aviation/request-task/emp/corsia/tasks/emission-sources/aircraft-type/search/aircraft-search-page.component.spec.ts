import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { EmissionSourcesCorsiaFormProvider } from '../../emission-sources-form.provider';
import { AircraftTypeFormProvider } from '../aircraft-type-form.provider';
import { AircraftSearchPageComponent } from './aircraft-search-page.component';

describe('AircraftSearchPage', () => {
  let result: RenderResult<AircraftSearchPageComponent>;

  beforeEach(async () => {
    result = await render(AircraftSearchPageComponent, {
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
