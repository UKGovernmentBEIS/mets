import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { FlightProceduresFormComponent } from './flight-procedures-form.component';

async function renderComponent() {
  const { fixture, detectChanges } = await render(
    `<form [formGroup]="form"><app-flight-procedures-form></app-flight-procedures-form></form>`,
    {
      imports: [ReactiveFormsModule, FlightProceduresFormComponent],
      componentProperties: {
        form: new FormGroup({
          procedureDescription: new FormControl<string>(null),
          procedureDocumentName: new FormControl<string>(null),
          procedureReference: new FormControl<string>(null),
          responsibleDepartmentOrRole: new FormControl<string>(null),
          locationOfRecords: new FormControl<string>(null),
          itSystemUsed: new FormControl<string>(null),
        }),
      },
    },
  );

  return { component: fixture.componentInstance, detectChanges };
}

describe('FlightProceduresFormComponent', () => {
  it('should create FlightProceduresFormComponent', async () => {
    const { component } = await renderComponent();

    expect(component).toBeTruthy();
  });
});
