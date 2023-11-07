import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import userEvent from '@testing-library/user-event';

import { ManagementProceduresSummaryTemplateComponent } from './management-procedures-summary-template.component';

describe('ManagementProceduresSummaryTemplateComponent', () => {
  async function renderComponent() {
    const { fixture, detectChanges } = await render(
      `
        <app-management-procedures-summary-template
          [data] = data
        >
        </app-management-procedures-summary-template>
      `,
      {
        imports: [ManagementProceduresSummaryTemplateComponent],
        componentProperties: {
          data: {
            monitoringReportingRoles: {
              monitoringReportingRoles: [
                {
                  jobTitle: 'test job',
                  mainDuties: 'test duties',
                },
              ],
            },
            recordKeepingAndDocumentation: {
              locationOfRecords: 'test loc',
              procedureReference: 'test ref',
              procedureDescription: 'test desc',
              procedureDocumentName: 'test doc',
              responsibleDepartmentOrRole: 'test resp',
            },
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

  it(`should display summary only for first two forms`, async () => {
    await renderComponent();
    expect(screen.getAllByText(/Monitoring and reporting roles/)).toBeInTheDocument;
    expect(screen.getAllByText(/Record keeping and documentation/)).toBeInTheDocument;
    expect(screen.queryByText(/Assignment of responsibilities/)).not.toBeInTheDocument;
  });
});
