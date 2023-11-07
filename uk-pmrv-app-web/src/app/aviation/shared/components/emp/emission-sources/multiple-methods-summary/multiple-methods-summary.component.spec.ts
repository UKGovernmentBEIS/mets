import { render, RenderResult } from '@testing-library/angular';

import { MultipleMethodsSummaryTemplateComponent } from './multiple-methods-summary.component';

let result: RenderResult<MultipleMethodsSummaryTemplateComponent>;
describe('MultipleMethodsComponent', () => {
  beforeEach(async () => {
    result = await render(MultipleMethodsSummaryTemplateComponent, {});
  });

  it('smoke test', () => {
    const {
      fixture: { componentInstance: component },
    } = result;
    expect(component).toBeTruthy();
  });
});
