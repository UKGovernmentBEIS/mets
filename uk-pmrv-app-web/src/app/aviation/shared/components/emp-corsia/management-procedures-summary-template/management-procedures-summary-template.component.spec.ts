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
            dataManagement: {
              description: 'description',
              dataFlowDiagram: {
                uuid: '216a37c4-729d-47c4-997b-ad84fd13af4d',
                file: {
                  name: 'fileUpload.pdf',
                },
              },
            },
            recordKeepingAndDocumentation: 'rfv23',
            riskExplanation: 'Explanation description',
            empRevisions: 'Revisions of emissions monitoring plan description',
            monitoringReportingRoles: [
              {
                jobTitle: 'Job role 1',
                mainDuties: 'main duties roles',
              },
            ],
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
    expect(screen.getAllByText(/Data management/)).toBeInTheDocument;
    expect(screen.getAllByText(/Documentation and record keeping plan/)).toBeInTheDocument;
    expect(screen.getAllByText(/Explanation of risks/)).toBeInTheDocument;
    expect(screen.getAllByText(/Revisions of emissions monitoring plan/)).toBeInTheDocument;
  });
});
