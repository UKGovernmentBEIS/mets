import { Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import userEvent from '@testing-library/user-event';

import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { AbbreviationsFormProvider } from '../abbreviations-form.provider';
import { AbbreviationsFormComponent } from './abbreviations-form.component';

async function setup() {
  return {
    user: userEvent.setup(),
    ...(await render(MockParentComponent)),
  };
}

@Component({
  selector: 'app-mock-parent',
  template: ` <form [formGroup]="form">
    <app-abbreviations-form [heading]="heading"></app-abbreviations-form>
  </form>`,
  standalone: true,
  imports: [ReactiveFormsModule, AbbreviationsFormComponent],
  providers: [{ provide: TASK_FORM_PROVIDER, useClass: AbbreviationsFormProvider }],
})
class MockParentComponent {
  form = inject<AbbreviationsFormProvider>(TASK_FORM_PROVIDER).form;
  heading = document.createElement('h1');
}

describe('AbbreviationsFormComponent', () => {
  it('should give the user the option to select if they want to provide abbreviations or not', async () => {
    await setup();
    expect(
      screen.getByText('Are you using any abbreviations or terminology in your application which need explanation?'),
    ).toBeTruthy();
    expect(yesOption()).toBeInTheDocument();
    expect(noOption()).toBeInTheDocument();
  });

  it(`should not have selected option`, async () => {
    await setup();
    expect(yesOption()).not.toBeChecked();
    expect(noOption()).not.toBeChecked();
  });

  it(`should show label 'Definition 1' when user selects 'Yes'`, async () => {
    const { user, detectChanges } = await setup();
    await user.click(yesOption());
    detectChanges();
    expect(screen.getByText(/Definition 1/)).toBeInTheDocument();
  });

  it('should give the option to add more definitions', async () => {
    const { user, detectChanges } = await setup();
    await user.click(yesOption());
    detectChanges();
    const addButton = screen.getByRole('button', { name: 'Add another' });
    expect(addButton).toBeInTheDocument();

    await user.click(addButton);
    detectChanges();
    expect(screen.getByText(/Definition 1/)).toBeInTheDocument();
    expect(screen.getByText(/Definition 2/)).toBeInTheDocument();
  });

  it('should give user option to remove any definition if more than one', async () => {
    const { user, detectChanges } = await setup();
    await user.click(yesOption());
    detectChanges();
    expect(screen.queryByRole('button', { name: /Remove/ })).not.toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: /Add another/ }));
    detectChanges();
    expect(screen.queryAllByRole('button', { name: /Remove/ })).toHaveLength(2);

    await user.click(screen.getAllByRole('button', { name: /Remove/ })[0]);
    detectChanges();
    expect(screen.queryByRole('button', { name: /Remove/ })).not.toBeInTheDocument();
    expect(screen.queryAllByText(/Definition \d+/)).toHaveLength(1);
  });

  function yesOption() {
    return screen.getByRole('radio', { name: /Yes/ });
  }

  function noOption() {
    return screen.getByRole('radio', { name: /No/ });
  }
});
