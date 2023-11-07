import { Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { ConfidentialityFormProvider } from '../confidentiality-form.provider';
import { ConfidentialityFormComponent } from './confidentiality-form.component';

async function setup() {
  return {
    user: userEvent.setup(),
    ...(await render(MockParentComponent)),
  };
}

@Component({
  selector: 'app-mock-parent',
  template: ` <form [formGroup]="form">
    <app-confidentiality-form></app-confidentiality-form>
  </form>`,
  standalone: true,
  imports: [ReactiveFormsModule, ConfidentialityFormComponent],
  providers: [{ provide: TASK_FORM_PROVIDER, useClass: ConfidentialityFormProvider }],
})
class MockParentComponent {
  form = inject<ConfidentialityFormProvider>(TASK_FORM_PROVIDER).form;
}

describe('ConfidentialityFormComponent', () => {
  it('should give the user the option to select to publish data', async () => {
    await setup();

    expect(yesOptions()[0]).toBeInTheDocument();
    expect(noOptions()[0]).toBeInTheDocument();

    expect(yesOptions()[1]).toBeInTheDocument();
    expect(noOptions()[1]).toBeInTheDocument();
  });

  it(`should not have selected option`, async () => {
    await setup();

    expect(yesOptions()[0]).not.toBeChecked();
    expect(noOptions()[0]).not.toBeChecked();

    expect(yesOptions()[1]).not.toBeChecked();
    expect(noOptions()[1]).not.toBeChecked();
  });

  function yesOptions() {
    return screen.getAllByRole('radio', { name: /Yes/ });
  }

  function noOptions() {
    return screen.getAllByRole('radio', { name: /No/ });
  }
});
