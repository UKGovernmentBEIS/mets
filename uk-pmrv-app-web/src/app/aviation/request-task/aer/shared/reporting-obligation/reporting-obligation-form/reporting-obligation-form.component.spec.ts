import { Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { ReportingObligationFormProvider } from '../reporting-obligation-form.provider';
import { ReportingObligationFormComponent } from './reporting-obligation-form.component';

async function setup() {
  return {
    user: userEvent.setup(),
    ...(await render(MockParentComponent)),
  };
}

@Component({
  selector: 'app-mock-parent',
  template: ` <form [formGroup]="form">
    <app-reporting-obligation-form [year]="header"></app-reporting-obligation-form>
  </form>`,
  standalone: true,
  imports: [ReactiveFormsModule, ReportingObligationFormComponent],
  providers: [{ provide: TASK_FORM_PROVIDER, useClass: ReportingObligationFormProvider }],
})
class MockParentComponent {
  form = inject<ReportingObligationFormProvider>(TASK_FORM_PROVIDER).form;
  header = 2023;
}

describe('ReportingObligationFormComponent', () => {
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
