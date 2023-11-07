import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { LocationOnShoreStateDTO } from 'pmrv-api';

import { LocationStateFormComponent } from './location-state-form.component';

describe('AddressFormComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `<form [formGroup]="form"><app-location-state-form></app-location-state-form></form>`,
      {
        imports: [ReactiveFormsModule, LocationStateFormComponent],
        componentProperties: {
          form: new FormGroup({
            type: new FormControl<LocationOnShoreStateDTO['type'] | null>('ONSHORE_STATE'),
            line1: new FormControl<LocationOnShoreStateDTO['line1'] | null>(null),
            line2: new FormControl<LocationOnShoreStateDTO['line2'] | null>(null),
            city: new FormControl<LocationOnShoreStateDTO['city'] | null>(null),
            state: new FormControl<LocationOnShoreStateDTO['state'] | null>(null),
            postcode: new FormControl<LocationOnShoreStateDTO['postcode'] | null>(null),
            country: new FormControl<LocationOnShoreStateDTO['country'] | null>(null),
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
