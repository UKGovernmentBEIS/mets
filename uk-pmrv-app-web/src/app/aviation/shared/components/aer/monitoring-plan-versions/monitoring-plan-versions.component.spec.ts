import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { AerMonitoringPlanVersionsComponent } from './monitoring-plan-versions.component';

describe('AerMonitoringPlanVersionsComponent', () => {
  async function renderComponent() {
    const { fixture, detectChanges } = await render(
      `
        <app-aer-monitoring-plan-versions
          [planVersions] = planVersions
        >
        </app-aer-monitoring-plan-versions>
      `,
      {
        imports: [AerMonitoringPlanVersionsComponent],
        componentProperties: {
          planVersions: [],
        },
      },
    );
    return {
      user: userEvent,
      component: fixture.componentInstance,
      detectChanges,
    };
  }

  async function renderComponentwithPlans() {
    const { fixture, detectChanges } = await render(
      `
        <app-aer-monitoring-plan-versions
          [planVersions] = planVersions
        >
        </app-aer-monitoring-plan-versions>
      `,
      {
        imports: [AerMonitoringPlanVersionsComponent],
        componentProperties: {
          planVersions: [
            {
              empId: '1UK-E-AV-11399',
              empApprovalDate: '2020-11-14',
              empConsolidationNumber: 13,
            },
          ],
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

  it(`should display correct header`, async () => {
    await renderComponent();
    expect(screen.getByText('Monitoring plan versions')).toBeInTheDocument();
  });

  it(`should display correct message for empty monitoring plans`, async () => {
    await renderComponent();
    expect(screen.getByText('No monitoring plans.')).toBeInTheDocument();
  });

  it(`should display correct message for existing monitoring plans`, async () => {
    await renderComponentwithPlans();
    expect(screen.getByText('1UK-E-AV-11399 v13')).toBeInTheDocument();
    expect(screen.getByText('14 Nov 2020')).toBeInTheDocument();
  });
});
