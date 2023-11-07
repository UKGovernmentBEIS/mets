import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { FuelUpliftRecordsFormComponent } from './fuel-uplift-records-form.component';

async function renderComponent() {
  const { fixture, detectChanges } = await render(
    `<form [formGroup]="form"><app-fuel-uplift-assignment-form></app-fuel-uplift-assignment-form></form>`,
    {
      imports: [ReactiveFormsModule, FuelUpliftRecordsFormComponent],
      componentProperties: {
        form: new FormGroup({
          zeroFuelUplift: new FormControl<string | null>(null),
        }),
      },
    },
  );

  return { component: fixture.componentInstance, detectChanges };
}

describe('FuelUpliftRecordsFormComponent', () => {
  it('should create', async () => {
    const { component } = await renderComponent();

    expect(component).toBeTruthy();
  });
});
