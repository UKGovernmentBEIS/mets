import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { EmissionsReductionClaimSummaryTemplateComponent } from './emissions-reduction-claim-summary-template.component';

describe('EmissionsReductionClaimSummaryTemplateComponent', () => {
  async function renderComponent() {
    const { fixture, detectChanges } = await render(
      `
        <app-emissions-reduction-claim-summary-template
          exist = false
          [data] = data
        >
        </app-emissions-reduction-claim-summary-template>
      `,
      {
        imports: [EmissionsReductionClaimSummaryTemplateComponent],
        componentProperties: {
          data: {
            exist: false,
          },
        },
      },
    );
    return {
      user: userEvent,
      component: fixture.componentInstance,
      detectChanges,
    };
  }

  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });

  it(`should display emissions reduction claim intention`, async () => {
    await renderComponent();
    expect(screen.getAllByText(/Are you intending to make an emissions reduction claim?/)).toHaveLength(1);
  });
});
