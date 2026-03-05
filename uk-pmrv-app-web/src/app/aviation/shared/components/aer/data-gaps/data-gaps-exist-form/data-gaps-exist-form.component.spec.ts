import { Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { DataGapsFormProvider } from '../../../../../request-task/aer/ukets/tasks/data-gaps/data-gaps-form.provider';
import { DataGapsExistFormComponent } from './data-gaps-exist-form.component';

async function setup() {
  return {
    user: userEvent.setup(),
    ...(await render(MockParentComponent)),
  };
}

@Component({
  selector: 'app-mock-parent',
  template: `
    <form [formGroup]="existGroup">
      <app-data-gaps-exist-form></app-data-gaps-exist-form>
    </form>
  `,
  standalone: true,
  imports: [ReactiveFormsModule, DataGapsExistFormComponent],
  providers: [{ provide: TASK_FORM_PROVIDER, useClass: DataGapsFormProvider }],
})
class MockParentComponent {
  existGroup = inject<DataGapsFormProvider>(TASK_FORM_PROVIDER).existGroup;
}

describe('DataGapsExistFormComponent', () => {
  it('should give the user the option to select if there have been any fuel use data gaps during the scheme year', async () => {
    await setup();

    expect(yesOption()).toBeInTheDocument();
    expect(noOption()).toBeInTheDocument();
  });

  it('should not have selected option', async () => {
    await setup();

    expect(yesOption()).not.toBeChecked();
    expect(noOption()).not.toBeChecked();
  });

  function yesOption() {
    return screen.getByRole('radio', { name: /Yes/ });
  }

  function noOption() {
    return screen.getByRole('radio', { name: /No/ });
  }
});
