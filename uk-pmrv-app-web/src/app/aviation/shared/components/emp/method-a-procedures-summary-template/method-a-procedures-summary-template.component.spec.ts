import { render, screen } from '@testing-library/angular';

import { MethodAProceduresSummaryTemplateComponent } from './method-a-procedures-summary-template.component';

describe('MethodAProceduresSummaryTemplateComponent', () => {
  async function renderComponent() {
    const { fixture, detectChanges } = await render(
      `
        <app-method-a-procedures-summary-template [data]="data">
        </app-method-a-procedures-summary-template>
      `,
      {
        imports: [MethodAProceduresSummaryTemplateComponent],
        componentProperties: {
          data: {
            fuelDensity: {
              locationOfRecords: 'Intranet',
              procedureReference: 'reference',
              procedureDescription: 'Procedure description',
              procedureDocumentName: 'document',
              responsibleDepartmentOrRole: 'Department or role',
            },
            fuelConsumptionPerFlight: {
              locationOfRecords: 'Intranet',
              procedureReference: 'reference',
              procedureDescription: 'Procedure description',
              procedureDocumentName: 'document',
              responsibleDepartmentOrRole: 'Department or role',
            },
          },
        },
      },
    );

    return {
      component: fixture.componentInstance,
      detectChanges,
    };
  }

  it('should create', async () => {
    const { component } = await renderComponent();

    expect(component).toBeTruthy();
  });

  it(`should display headers`, async () => {
    await renderComponent();

    expect(screen.getByText('Monitoring fuel consumption per flight')).toBeInTheDocument();
    expect(screen.getByText('Measuring fuel density')).toBeInTheDocument();
  });
});
