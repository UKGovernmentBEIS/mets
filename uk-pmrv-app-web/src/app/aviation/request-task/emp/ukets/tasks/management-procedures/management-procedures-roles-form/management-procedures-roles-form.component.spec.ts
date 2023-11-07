import { Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';
import { ManagementProceduresRolesFormComponent } from './management-procedures-roles-form.component';

@Component({
  selector: 'app-mock-parent',
  template: ` <form [formGroup]="form">
    <app-management-procedures-roles-form [heading]="heading"></app-management-procedures-roles-form>
  </form>`,
  standalone: true,
  imports: [ReactiveFormsModule, ManagementProceduresRolesFormComponent],
  providers: [{ provide: TASK_FORM_PROVIDER, useClass: ManagementProceduresFormProvider }],
})
class MockParentComponent {
  form = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER).form;
  heading = document.createElement('h1');
}

describe('ManagementProceduresRolesFormComponent', () => {
  beforeEach(async () => {
    await render(MockParentComponent);
  });

  it(`should show label 'Role 1' and add button but no remove button`, () => {
    expect(screen.getByText(/Role 1/)).toBeInTheDocument();

    expect(screen.queryByRole('button', { name: /Add another/ })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /Remove/ })).not.toBeInTheDocument();
    expect(screen.getByText(/Job title/)).toBeInTheDocument();
    expect(screen.getByText(/Main duties/)).toBeInTheDocument();
  });
});
