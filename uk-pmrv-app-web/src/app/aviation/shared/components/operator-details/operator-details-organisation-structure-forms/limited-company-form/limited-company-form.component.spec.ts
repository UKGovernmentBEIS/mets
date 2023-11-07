import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { LimitedCompanyOrganisation, LocationOnShoreStateDTO } from 'pmrv-api';

import { LimitedCompanyFormComponent } from './limited-company-form.component';

describe('LimitedCompanyFormComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <form [formGroup]="form">
        <app-organisation-structure-limited-company-form></app-organisation-structure-limited-company-form>
      </form>
      `,
      {
        imports: [ReactiveFormsModule, LimitedCompanyFormComponent],
        componentProperties: {
          form: new FormGroup({
            registrationNumber: new FormControl<LimitedCompanyOrganisation['registrationNumber'] | null>(null),
            organisationLocation: new FormGroup({
              type: new FormControl<LocationOnShoreStateDTO['type'] | null>('ONSHORE_STATE'),
              line1: new FormControl<LocationOnShoreStateDTO['line1'] | null>(null),
              line2: new FormControl<LocationOnShoreStateDTO['line2'] | null>(null),
              city: new FormControl<LocationOnShoreStateDTO['city'] | null>(null),
              state: new FormControl<LocationOnShoreStateDTO['state'] | null>(null),
              postcode: new FormControl<LocationOnShoreStateDTO['postcode'] | null>(null),
              country: new FormControl<LocationOnShoreStateDTO['country'] | null>(null),
            }),
            evidenceFiles: new FormControl<LimitedCompanyOrganisation['evidenceFiles'] | null>(null),
            differentContactLocationExist: new FormControl<
              LimitedCompanyOrganisation['differentContactLocationExist'] | null
            >(null),
            differentContactLocation: new FormGroup({
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
