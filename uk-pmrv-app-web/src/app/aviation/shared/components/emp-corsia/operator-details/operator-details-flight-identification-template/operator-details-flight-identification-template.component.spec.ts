import { FormControl, FormGroup } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { render } from '@testing-library/angular';

import { FlightIdentification } from 'pmrv-api';

import { OperatorDetailsFlightIdentificationTemplateComponent } from './operator-details-flight-identification-template.component';

describe('OperatorDetailsFlightIdentificationTemplateComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <app-operator-details-flight-identification-template
        [form]="form"
      ></app-operator-details-flight-identification-template>
      `,
      {
        imports: [RouterTestingModule, OperatorDetailsFlightIdentificationTemplateComponent],
        componentProperties: {
          form: new FormGroup({
            flightIdentificationType: new FormControl<FlightIdentification['flightIdentificationType']>(null),
            icaoDesignators: new FormControl<FlightIdentification['icaoDesignators']>(null),
            aircraftRegistrationMarkings: new FormControl<FlightIdentification['aircraftRegistrationMarkings'] | null>(
              null,
            ),
          }),
        },
      },
    );

    return { component: fixture.componentInstance, detectChanges };
  };

  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });
});
