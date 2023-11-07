import { FormArray, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { LocationOnShoreStateDTO, PartnershipOrganisation } from 'pmrv-api';

import { PartnershipFormComponent } from './partnership-form.component';

describe('PartnershipFormComponent', () => {
  const form = new FormGroup({
    partnershipName: new FormControl<PartnershipOrganisation['partnershipName'] | null>(null),
    organisationLocation: new FormGroup({
      type: new FormControl<LocationOnShoreStateDTO['type'] | null>('ONSHORE_STATE'),
      line1: new FormControl<LocationOnShoreStateDTO['line1'] | null>(null),
      line2: new FormControl<LocationOnShoreStateDTO['line2'] | null>(null),
      city: new FormControl<LocationOnShoreStateDTO['city'] | null>(null),
      state: new FormControl<LocationOnShoreStateDTO['state'] | null>(null),
      postcode: new FormControl<LocationOnShoreStateDTO['postcode'] | null>(null),
      country: new FormControl<LocationOnShoreStateDTO['country'] | null>(null),
    }),
    partners: new FormArray<FormControl<string>>([]),
  });

  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <form [formGroup]="form">
        <app-organisation-structure-partnership-form
          [partnersControl]="partnersControl"
          [isEditable]="true"
        ></app-organisation-structure-partnership-form>
      </form>
      `,
      {
        imports: [ReactiveFormsModule, PartnershipFormComponent],
        componentProperties: {
          form,
          partnersControl: form.get('partners') as FormArray,
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
