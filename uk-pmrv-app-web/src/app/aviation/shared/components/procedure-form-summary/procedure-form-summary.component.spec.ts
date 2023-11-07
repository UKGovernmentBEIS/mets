import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { ProcedureFormSummaryComponent } from './procedure-form-summary.component';

describe('ProcedureFormSummaryComponent', () => {
  async function renderComponent() {
    const { fixture, detectChanges } = await render(
      `
        <app-procedure-form-summary
          header="Page heading"
          [procedureFormData] = procedureFormData
        >
        </app-procedure-form-summary>
      `,
      {
        imports: [ProcedureFormSummaryComponent],
        componentProperties: {
          procedureFormData: {
            procedureDescription: 'test description',
            procedureDocumentName: 'test document name',
            procedureReference: 'test reference',
            responsibleDepartmentOrRole: 'test department role',
            locationOfRecords: 'test location',
            itSystemUsed: 'test system',
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

  it('should display provided header', async () => {
    await renderComponent();
    expect(screen.getByRole('heading', { name: /Page heading/ })).toBeVisible();
  });

  it('should show procedure summary headers info', async () => {
    await renderComponent();
    expect(screen.getAllByText(/Procedure description/)).toHaveLength(1);
    expect(screen.getAllByText(/Name of the procedure document/)).toHaveLength(1);
    expect(screen.getAllByText(/Procedure reference/)).toHaveLength(1);
    expect(screen.getAllByText(/Department or role responsible for data maintenance/)).toHaveLength(1);
    expect(screen.getAllByText(/Location of records/)).toHaveLength(1);
    expect(screen.getAllByText(/IT system used/)).toHaveLength(1);
  });

  it('should show procedure summary values info', async () => {
    await renderComponent();
    expect(screen.getAllByText(/test description/)).toHaveLength(1);
    expect(screen.getAllByText(/test document name/)).toHaveLength(1);
    expect(screen.getAllByText(/test reference/)).toHaveLength(1);
    expect(screen.getAllByText(/test department role/)).toHaveLength(1);
    expect(screen.getAllByText(/test location/)).toHaveLength(1);
    expect(screen.getAllByText(/test system/)).toHaveLength(1);
  });
});
