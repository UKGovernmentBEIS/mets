import { Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import userEvent from '@testing-library/user-event';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
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
  template: ` <form [formGroup]="form">
    <app-emissions-reduction-claim-form></app-emissions-reduction-claim-form>
  </form>`,
  standalone: true,
  imports: [ReactiveFormsModule, EmissionsReductionClaimFormComponent],
  providers: [{ provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider }],
})
class MockParentComponent {
  form = new FormGroup({
    exist: inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER).existCtrl,
  });
}

describe('EmissionsReductionClaimFormComponent', () => {
  it('should give the user the option to select if they want to provide emissions reduction claim or not', async () => {
    await setup();
    expect(
      screen.getByText(
        'Will you be making an emissions reduction claim as a result of the purchase and delivery of SAF?',
      ),
    ).toBeTruthy();
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
