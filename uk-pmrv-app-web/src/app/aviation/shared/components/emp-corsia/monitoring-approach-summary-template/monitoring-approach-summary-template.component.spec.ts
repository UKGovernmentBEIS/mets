import { render } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { MonitoringApproachSummaryTemplateComponent } from './monitoring-approach-summary-template.component';

describe('MonitoringApproachSummaryTemplateComponent', () => {
  async function renderComponent() {
    const { fixture, detectChanges } = await render(
      `
        <app-monitoring-approach-summary-template
          [data] = data
        >
        </app-monitoring-approach-summary-template>
      `,
      {
        imports: [MonitoringApproachSummaryTemplateComponent],
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
});
