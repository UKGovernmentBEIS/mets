import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render, screen } from '@testing-library/angular';

import { ProcedureFormComponent } from './procedure-form.component';

async function renderComponent() {
  const { fixture, detectChanges } = await render(
    `<form [formGroup]="form"><app-procedure-form></app-procedure-form></form>`,
    {
      imports: [ReactiveFormsModule, ProcedureFormComponent],
      componentProperties: {
        form: new FormGroup({
          procedureDescription: new FormControl<string>(null),
          procedureDocumentName: new FormControl<string>(null),
          procedureReference: new FormControl<string>(null),
          responsibleDepartmentOrRole: new FormControl<string>(null),
          locationOfRecords: new FormControl<string>(null),
          itSystemUsed: new FormControl<string>(null),
        }),
      },
    },
  );

  return { component: fixture.componentInstance, detectChanges };
}

describe('ProcedureFormComponent', () => {
  it('should create', async () => {
    const { component } = await renderComponent();
    screen.debug();
    expect(component).toBeTruthy();
  });

  it('should show procedure description field', async () => {
    await renderComponent();
    expect(screen.getByLabelText(/Procedure description/)).toBeVisible();
  });

  it('should show procedure document name field', async () => {
    await renderComponent();
    expect(screen.getByLabelText(/Name of the procedure document/)).toBeVisible();
  });

  it('should show procedure reference field', async () => {
    await renderComponent();
    expect(screen.getByLabelText(/Procedure reference/)).toBeVisible();
  });

  it('should show department or role field', async () => {
    await renderComponent();
    expect(screen.getByLabelText(/Department or role responsible for data maintenance/)).toBeVisible();
  });

  it('should show location of records field', async () => {
    await renderComponent();
    expect(screen.getByLabelText(/Location of records/)).toBeVisible();
  });

  it('should show it system field', async () => {
    await renderComponent();
    expect(screen.getByLabelText(/IT system used/)).toBeVisible();
  });
});
