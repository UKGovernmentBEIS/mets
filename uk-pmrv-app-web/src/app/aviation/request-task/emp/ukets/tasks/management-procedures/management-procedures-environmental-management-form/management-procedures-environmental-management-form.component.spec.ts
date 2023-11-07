import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import userEvent from '@testing-library/user-event';

import { ManagementProceduresEnvironmentalManagementFormComponent } from './management-procedures-environmental-management-form.component';

async function renderComponent() {
  const { fixture, detectChanges } = await render(
    `<form [formGroup]="form"><app-management-procedures-environmental-management-form></app-management-procedures-environmental-management-form></form>`,
    {
      imports: [ReactiveFormsModule, ManagementProceduresEnvironmentalManagementFormComponent],
      componentProperties: {
        form: new FormGroup({
          environmentalManagementSystem: new FormGroup({
            exist: new FormControl<string>(null),
            certified: new FormControl<string>(null),
            certificationStandard: new FormControl<string>(null),
          }),
        }),
      },
    },
  );

  return { user: userEvent, component: fixture.componentInstance, detectChanges };
}

describe('ManagementProceduresEnvironmentalManagementFormComponent', () => {
  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });

  it('should be able to select whether the organisation has an environmental management system', async () => {
    await renderComponent();

    expect(screen.getByText('Does the organisation have a documented environmental management system?')).toBeTruthy();
    expect(yesOption()).toBeInTheDocument();
    expect(noOption()).toBeInTheDocument();
  });

  it(`should not have selected option`, async () => {
    await renderComponent();

    expect(yesOption()).not.toBeChecked();
    expect(noOption()).not.toBeChecked();
  });

  it('should be able to select whether it is externally certified, after selecting yes in the first option', async () => {
    const { user, detectChanges } = await renderComponent();

    user.click(yesOption());
    detectChanges();

    expect(screen.getByText('Is the system externally certified?')).toBeTruthy();
    expect(screen.queryAllByRole('radio', { name: /Yes/ })).toHaveLength(2);
    expect(screen.queryAllByRole('radio', { name: /No/ })).toHaveLength(2);
  });

  it('should be able to set the standard, after selecting yes in the second option', async () => {
    const { user, detectChanges } = await renderComponent();

    user.click(yesOption());
    detectChanges();

    userEvent.click(yesOption2());
    detectChanges();

    expect(screen.getByText('Standard to which the system is certified')).toBeTruthy();
    expect(screen.queryAllByRole('radio', { name: /Yes/ })).toHaveLength(2);
    expect(screen.queryAllByRole('radio', { name: /No/ })).toHaveLength(2);
  });

  function yesOption() {
    return screen.queryAllByRole('radio', { name: /Yes/ })[0];
  }

  function yesOption2() {
    return screen.queryAllByRole('radio', { name: /Yes/ })[1];
  }

  function noOption() {
    return screen.queryAllByRole('radio', { name: /No/ })[0];
  }
});
