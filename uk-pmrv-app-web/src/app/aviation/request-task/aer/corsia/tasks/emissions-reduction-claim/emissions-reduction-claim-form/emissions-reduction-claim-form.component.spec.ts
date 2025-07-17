import { Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { EmissionsReductionClaimFormComponent } from './emissions-reduction-claim-form.component';

async function setup() {
  return {
    user: userEvent.setup(),
    ...(await render(MockParentComponent)),
  };
}

@Component({
  selector: 'app-mock-parent',
  template: `
    <form [formGroup]="form">
      <app-emissions-reduction-claim-form></app-emissions-reduction-claim-form>
    </form>
  `,
  standalone: true,
  imports: [ReactiveFormsModule, EmissionsReductionClaimFormComponent],
  providers: [{ provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider }],
})
class MockParentComponent {
  form = inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER).form;
}

describe('EmissionsReductionClaimFormComponent', () => {
  it('should give the user the option to select if they are required to submit emissions report', async () => {
    await setup();

    expect(yesOption()).toBeInTheDocument();
    expect(noOption()).toBeInTheDocument();
  });

  it(`should not have selected option`, async () => {
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
