import { render, RenderResult } from '@testing-library/angular';

import { AircraftTypeFormProvider } from '../aircraft-type/aircraft-type-form.provider';
import { EmissionSourcesFormProvider } from '../emission-sources-form.provider';
import { EmissionFactorsSummaryComponent } from './emission-factors-summary.component';

describe('EmissionFactorsSummaryComponent', () => {
  let result: RenderResult<EmissionFactorsSummaryComponent>;

  beforeEach(async () => {
    result = await render(EmissionFactorsSummaryComponent, {
      providers: [EmissionSourcesFormProvider, AircraftTypeFormProvider],
    });
  });

  it('smoke test', () => {
    const {
      fixture: { componentInstance: component },
    } = result;
    expect(component).toBeTruthy();
  });
});
