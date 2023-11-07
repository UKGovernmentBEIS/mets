import { render, RenderResult } from '@testing-library/angular';

import { AircraftTypeFormProvider } from '../aircraft-type/aircraft-type-form.provider';
import { EmissionSourcesCorsiaFormProvider } from '../emission-sources-form.provider';
import { EmissionFactorsSummaryComponent } from './emission-factors-summary.component';

describe('EmissionFactorsSummaryComponent', () => {
  let result: RenderResult<EmissionFactorsSummaryComponent>;

  beforeEach(async () => {
    result = await render(EmissionFactorsSummaryComponent, {
      providers: [EmissionSourcesCorsiaFormProvider, AircraftTypeFormProvider],
    });
  });

  it('smoke test', () => {
    const {
      fixture: { componentInstance: component },
    } = result;
    expect(component).toBeTruthy();
  });
});
