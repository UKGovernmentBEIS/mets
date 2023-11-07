import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { MonitoringApproachTypeFormComponent } from '../monitoring-approach-type-form';

async function renderComponent() {
  const { fixture, detectChanges } = await render(
    `<form [formGroup]="form"><app-simplified-approach-form [vm]="vm"></app-simplified-approach-form></form>`,
    {
      imports: [ReactiveFormsModule, MonitoringApproachTypeFormComponent],
      componentProperties: {
        form: new FormGroup({
          explanation: new FormControl<string | null>(null),
          supportingEvidenceFiles: new FormControl<any>(null),
        }),
      },
    },
  );

  return { component: fixture.componentInstance, detectChanges };
}

describe('SimplifiedApproachFormComponent', () => {
  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });
});
