import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { ManagementProceduresDataFlowFormComponent } from './management-procedures-data-flow-form.component';

async function renderComponent() {
  const { fixture, detectChanges } = await render(
    `<form [formGroup]="form"><app-management-procedures-data-flow-form></app-management-procedures-data-flow-form></form>`,
    {
      imports: [ReactiveFormsModule, ManagementProceduresDataFlowFormComponent],
      componentProperties: {
        form: new FormGroup({
          dataFlowActivities: new FormGroup({
            procedureDescription: new FormControl<string>(null),
            procedureDocumentName: new FormControl<string>(null),
            procedureReference: new FormControl<string>(null),
            responsibleDepartmentOrRole: new FormControl<string>(null),
            locationOfRecords: new FormControl<string>(null),
            itSystemUsed: new FormControl<string>(null),
            diagramReference: new FormControl<string | null>(null),
            otherStandardsApplied: new FormControl<string | null>(null),
            primaryDataSources: new FormControl<string | null>(null),
            processingSteps: new FormControl<string | null>(null),
            diagramAttachmentId: new FormControl<string | null>(null),
          }),
        }),
      },
    },
  );

  return { component: fixture.componentInstance, detectChanges };
}

describe('ManagementProceduresDataFlowFormComponent', () => {
  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });

  it(`should display both procedure form details and custom ones`, async () => {
    await renderComponent();
    expect(screen.getByText('Procedure description')).toBeInTheDocument();
    expect(screen.getByText('Name of the procedure document')).toBeInTheDocument();
    expect(screen.getByText('Primary data sources')).toBeInTheDocument();
    expect(screen.getByText('Describe the processing steps for each data flow activity')).toBeInTheDocument();
    expect(screen.getByText('Upload a data flow diagram')).toBeInTheDocument();
  });
});
