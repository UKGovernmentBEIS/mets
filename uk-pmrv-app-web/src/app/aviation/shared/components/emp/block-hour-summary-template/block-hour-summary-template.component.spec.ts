import { render, RenderResult } from '@testing-library/angular';

import { BlockHourSummaryTemplateComponent } from './block-hour-summary-template.component';

describe('BlockHourSummaryTemplateComponent', () => {
  let result: RenderResult<BlockHourSummaryTemplateComponent>;
  beforeEach(async () => {
    result = await render(BlockHourSummaryTemplateComponent);
  });

  it('should create', () => {
    const {
      fixture: { componentInstance: component },
    } = result;
    expect(component).toBeTruthy();
  });
});
