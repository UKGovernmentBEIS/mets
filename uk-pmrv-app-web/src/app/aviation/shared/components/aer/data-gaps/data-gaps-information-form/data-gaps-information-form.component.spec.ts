import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render, screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { GovukValidators } from 'govuk-components';

import { AviationAerDataGap } from 'pmrv-api';

import { DataGapsInformationFormComponent } from './data-gaps-information-form.component';

async function setup() {
  return {
    user: userEvent.setup(),
    ...(await render(MockParentComponent)),
  };
}

@Component({
  selector: 'app-mock-parent',
  template: `
    <form [formGroup]="dataGapsGroup">
      <app-data-gaps-information-form></app-data-gaps-information-form>
    </form>
  `,
  standalone: true,
  imports: [ReactiveFormsModule, DataGapsInformationFormComponent],
})
class MockParentComponent {
  dataGapsGroup = new FormGroup(
    {
      reason: new FormControl<AviationAerDataGap['reason'] | null>(null, [
        GovukValidators.required('Enter a reason for the data gap'),
        GovukValidators.maxLength(2000, 'The description should not be more than 2000 characters'),
      ]),
      type: new FormControl<AviationAerDataGap['type'] | null>(null, [
        GovukValidators.required('Enter the type of the data gap'),
        GovukValidators.maxLength(500, 'The description should not be more than 500 characters'),
      ]),
      replacementMethod: new FormControl<AviationAerDataGap['replacementMethod'] | null>(null, [
        GovukValidators.required('Enter the replacement method used for determining surrogate data'),
        GovukValidators.maxLength(2000, 'The description should not be more than 2000 characters'),
      ]),
      flightsAffected: new FormControl<AviationAerDataGap['flightsAffected'] | null>(null, [
        GovukValidators.required('Enter the number of flights affected by the data gap'),
        GovukValidators.positiveNumber('You must enter a positive number'),
        GovukValidators.integerNumber(),
      ]),
      totalEmissions: new FormControl<AviationAerDataGap['totalEmissions'] | null>(null, [
        GovukValidators.required('Enter the total emissions affected by the data gap'),
        GovukValidators.positiveNumber('You must enter a positive number'),
        GovukValidators.maxDecimalsValidator(3),
      ]),
    },
    { updateOn: 'change' },
  );
}

describe('DataGapsInformationFormComponent', () => {
  it('should display all form fields', async () => {
    await setup();

    expect(screen.getByLabelText(/Reason for the data gap/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Type of data gap/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Replacement method for determining surrogate data/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Number of flights affected by the data gap/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Total emissions affected by the data gap/)).toBeInTheDocument();
  });
});
