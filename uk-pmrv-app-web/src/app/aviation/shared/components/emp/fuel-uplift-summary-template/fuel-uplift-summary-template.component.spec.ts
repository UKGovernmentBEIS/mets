import { render, RenderResult } from '@testing-library/angular';

import { FuelUpliftSummaryTemplateComponent } from './fuel-uplift-summary-template.component';

describe('FuelUpliftSummaryTemplateComponent', () => {
  let result: RenderResult<FuelUpliftSummaryTemplateComponent>;
  beforeEach(async () => {
    result = await render(FuelUpliftSummaryTemplateComponent);
  });

  it('should create', () => {
    const {
      fixture: { componentInstance: component },
    } = result;
    expect(component).toBeTruthy();
  });
});
