import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { FileUpload } from '@shared/file-input/file-upload-event';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { ManagementProceduresDataFormComponent } from './management-procedures-data-form.component';

async function renderComponent() {
  const { fixture, detectChanges } = await render(
    `<form [formGroup]="form"><app-management-procedures-data-form></app-management-procedures-data-form></form>`,
    {
      imports: [ReactiveFormsModule, ManagementProceduresDataFormComponent],
      componentProperties: {
        form: new FormGroup({
          description: new FormControl<string>(null),
          dataFlowDiagram: new FormControl<FileUpload>(null),
        }),
      },
    },
  );

  return { component: fixture.componentInstance, detectChanges };
}

describe('ManagementProceduresDataFormComponent', () => {
  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });

  it(`should display the correct details`, async () => {
    await renderComponent();
    expect(screen.getByText('Procedure description')).toBeInTheDocument();
    expect(screen.getByText('Upload a data flow diagram')).toBeInTheDocument();
    expect(
      screen.getByText(
        'Attach a diagram that summarises the data sources, who is responsible for retrieving and processing the data, and the systems used to store the data.',
      ),
    ).toBeInTheDocument();
  });
});
