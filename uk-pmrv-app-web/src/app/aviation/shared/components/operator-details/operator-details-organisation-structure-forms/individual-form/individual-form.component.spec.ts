import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { IndividualOrganisation, LocationOnShoreStateDTO } from 'pmrv-api';

import { IndividualFormComponent } from './individual-form.component';

describe('IndividualFormComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <form [formGroup]="form">
        <app-organisation-structure-individual-form></app-organisation-structure-individual-form>
      </form>
      `,
      {
        imports: [ReactiveFormsModule, IndividualFormComponent],
        componentProperties: {
          form: new FormGroup({
            fullName: new FormControl<IndividualOrganisation['fullName'] | null>(null),
            organisationLocation: new FormGroup({
              type: new FormControl<LocationOnShoreStateDTO['type'] | null>('ONSHORE_STATE'),
              line1: new FormControl<LocationOnShoreStateDTO['line1'] | null>(null),
              line2: new FormControl<LocationOnShoreStateDTO['line2'] | null>(null),
              city: new FormControl<LocationOnShoreStateDTO['city'] | null>(null),
              state: new FormControl<LocationOnShoreStateDTO['state'] | null>(null),
              postcode: new FormControl<LocationOnShoreStateDTO['postcode'] | null>(null),
              country: new FormControl<LocationOnShoreStateDTO['country'] | null>(null),
            }),
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
